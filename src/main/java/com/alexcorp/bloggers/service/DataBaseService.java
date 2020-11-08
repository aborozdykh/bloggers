package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.dto.users.UserSignupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service
public class DataBaseService {

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() throws Throwable {
        if(userService.getUserByLogin("alexwhitecorp@gmail.com") == null) {
            UserSignupDto userDto = new UserSignupDto();
            userDto.setEmail("alexwhitecorp@gmail.com");
            userDto.setPhone("0679384857");
            userDto.setName("Alex");
            userDto.setSurname("White");
            userDto.setCity("Kiev");
            userDto.setBirthDate(new Date(2001, 04, 01));

            User user = userService.registerUser(userDto);
            user = userService.setPassword(user, "q");
        }
    }

}
