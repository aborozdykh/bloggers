package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.domain.ConfirmCode;
import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.domain.users.Blogger;
import com.alexcorp.bloggers.domain.users.Business;
import com.alexcorp.bloggers.dto.users.BloggerSignupDto;
import com.alexcorp.bloggers.dto.users.BusinessSignupDto;
import com.alexcorp.bloggers.dto.users.LoginUserDto;
import com.alexcorp.bloggers.dto.users.UserSignupDto;
import com.alexcorp.bloggers.repository.UserRepository;
import com.alexcorp.bloggers.utils.ValidateUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.NumberUtils;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

import static com.alexcorp.bloggers.utils.ValidateUtils.isPhoneNumber;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmCodeService codeService;


    public User getUserByLogin(String login) {
        LoginFormat loginFormat = new LoginFormat(login);
        User user = null;
        if (loginFormat.type == LoginType.PHONE) {
            user = userRepository.findByPhoneEndingWith(loginFormat.getValue());
        }
        else if (loginFormat.type == LoginType.EMAIL) {
            user = userRepository.findByEmail(loginFormat.getValue());
        }

        return user;
    }


    ///// Sign Up /////
    public User registerUser(UserSignupDto userDto) throws Throwable {
        String email = userDto.getEmail();
        String phone = ValidateUtils.validatePhoneNumber(userDto.getPhone());

        if(userRepository.existsByEmailOrPhoneEndingWith(email, phone)) {
            throw new Throwable("USER_ALREADY_EXIST");
        }

        User user;
        if (userDto instanceof BloggerSignupDto) {
            user = new Blogger((BloggerSignupDto)userDto);
            user.setRoles(Collections.singleton(User.Role.BLOGGER));
        }
        else if (userDto instanceof BusinessSignupDto) {
            user = new Business((BusinessSignupDto) userDto);
            user.setRoles(Collections.singleton(User.Role.BUSINESS));
        }
        else {
            user = new User(userDto);
            user.setRoles(Collections.singleton(User.Role.ADMIN));
        }

        user = userRepository.save(user);

        codeService.generateConfirmCode(user, ConfirmCode.ConfirmType.SIGNUP);

        return user;
    }

    //Google oauth
    public User registerUser(Map<String, Object> profile) throws Throwable {
        if(userRepository.existsByEmail((String) profile.get("email"))
                || userRepository.existsByOauthId((String) profile.get("sub"))) {
            throw new Throwable("USER_ALREADY_EXIST");
        }

        User user = new User();

        user.setOauthId((String) profile.get("sub"));
        user.setEmail((String) profile.get("email"));

        user.setName((String) profile.get("given_name"));
        user.setSurname((String) profile.get("family_name"));

        user.setActive(User.Active.ACTIVE);

        user =  userRepository.save(user);

        return authenticateUser(user);
    }

    public boolean checkSignupConfirmCode(Integer code) {
        return codeService.checkConfirmCode(code, ConfirmCode.ConfirmType.SIGNUP);
    }

    public User setPassword(User user, String password) throws Throwable {
        if(user == null || user.getActive().equals(User.Active.BANNED)) {
            throw new Throwable("USER_SET_PASS_ERROR");
        }

        password = passwordEncoder.encode(password);
        user.setPassword(password);
        user.setActive(User.Active.ACTIVE);

        return userRepository.save(user);
    }
    ///// Sign Up /////

    public User loginUser(LoginUserDto userDto) throws Throwable {
        User user = getUserByLogin(userDto.getLogin());
        if(user == null) throw new Throwable("USER_NOT_FOUND_OR_WRONG_PASSWORD");

        if(user.getActive().equals(User.Active.BANNED)) {
            throw new Throwable("USER_BANNED");
        }

        if(user.getActive().equals(User.Active.NON_ACTIVE)) {
            throw new Throwable("USER_NON_ACTIVE");
        }

        if(!passwordEncoder.matches(userDto.getPassword(), user.getPassword())){
            throw new Throwable("USER_NOT_FOUND_OR_WRONG_PASSWORD");
        }

        return authenticateUser(user);
    }

    public User loginUser(String oauthId) {
        User user = userRepository.findByOauthId(oauthId);
        if(user == null) return null;

        return authenticateUser(user);
    }

    private User authenticateUser(User user) {
        user.setLastLogin(LocalDateTime.now());
        user = userRepository.save(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return user;
    }

    @Data
    private class LoginFormat {

        private String value;
        private LoginType type;

        public LoginFormat(String login) {
            if (isPhoneNumber(login)) {
                value = ValidateUtils.validatePhoneNumber(login);
                type = LoginType.PHONE;
            } else {
                value = login;
                type = LoginType.EMAIL;
            }
        }
    }

    private enum LoginType {
        EMAIL, PHONE
    }
}
