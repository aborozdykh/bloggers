package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.dto.ErrorDto;
import com.alexcorp.bloggers.dto.users.BloggerSignupDto;
import com.alexcorp.bloggers.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserCrudController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String UPDATE_RES = "Profile update | Login: %s | Status: %s";

    @Autowired
    private UserService userService;

    @PutMapping(value = "/v1/crud/blogger/update")
    ResponseEntity signin(@Validated
                          @RequestBody BloggerSignupDto userDto) {
        try {
            User user = userService.updateProfileInfo(userDto);

            logger.info(String.format(UPDATE_RES, userDto.getEmail(), "Success"));
            return new ResponseEntity<> (user, HttpStatus.OK);
        }
        catch (Throwable throwable) {
            logger.info(String.format(UPDATE_RES, userDto.getEmail(), throwable.getMessage()));
            return new ResponseEntity<>(new ErrorDto(throwable.getMessage()), HttpStatus.OK);
        }
    }

}
