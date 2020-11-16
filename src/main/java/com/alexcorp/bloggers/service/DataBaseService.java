package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.dto.users.UserSignupDto;
import com.alexcorp.bloggers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service
public class DataBaseService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() throws Throwable {
        if(userService.getUserByLogin("admin@gmail.com") == null) {
            UserSignupDto userDto = new UserSignupDto();
            userDto.setEmail("admin@gmail.com");
            userDto.setPhone("0679384857");
            userDto.setPassword("q");
            userDto.setName("Alex");
            userDto.setSurname("White");
            userDto.setCity("Kiev");
            userDto.setBirthDate(new Date(2001, 04, 01));

            User user = userService.registerUser(userDto);
            user.setActive(User.Active.ACTIVE);

            userRepository.save(user);
        }
    }

}
