package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.ConfirmCode;
import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.dto.ErrorDto;
import com.alexcorp.bloggers.dto.users.*;
import com.alexcorp.bloggers.service.ConfirmCodeService;
import com.alexcorp.bloggers.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthorityController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String LOGIN_RES = "Sign In | Login: %s | Status: %s";
    private final static String REGISTRATION_RES = "Sign Up | E-Mail: %s, Phone: %s | Status: %s";
    private final static String PASS_SET_RES = "Password setting | E-Mail: %s, Code: %s | Status: %s";

    @Autowired
    private UserService userService;

    @Autowired
    private ConfirmCodeService codeService;

    @PostMapping(value = "/v1/signin/processing")
    ResponseEntity signin(@Validated
                          @RequestBody LoginUserDto userDto) {
        try {
            User user = userService.loginUser(userDto);

            logger.info(String.format(LOGIN_RES, userDto.getLogin(), "Success"));
            return new ResponseEntity<> (user, HttpStatus.OK);
        }
        catch (Throwable throwable) {
            logger.info(String.format(LOGIN_RES, userDto.getLogin(), throwable.getMessage()));
            return new ResponseEntity<>(new ErrorDto(throwable.getMessage()), HttpStatus.OK);
        }
    }

    @PostMapping(value = "/v1/signup/blogger")
    ResponseEntity signupBlogger(@Validated
                                 @RequestBody BloggerSignupDto userDto) {
        return handleSignup(userDto);
    }

    @PostMapping(value = "/v1/signup/business")
    ResponseEntity signupBusiness(@Validated
                                  @RequestBody BusinessSignupDto userDto) {
        return handleSignup(userDto);
    }

    @GetMapping(value = "/v1/signup/confirm")
    ResponseEntity signupConfirm(@RequestParam Integer code) {
        if(userService.checkSignupConfirmCode(code)) {
            return new ResponseEntity(HttpStatus.OK);
        }

        return new ResponseEntity<>(new ErrorDto("CODE_NOT_FOUND"), HttpStatus.OK);
    }

    @PostMapping(value = "/v1/signup/confirm")
    ResponseEntity signupConfirm(@Validated
                                 @RequestBody UserSignupConfirmDto confirmDto) {
        User user = null;
        try {
            ConfirmCode confirmCode = codeService.getConfirmCode(confirmDto.getCode(), ConfirmCode.ConfirmType.SIGNUP);
            user = confirmCode.getUser();

            userService.setPassword(user, confirmDto.getPassword());
            codeService.removeConfirmCode(confirmCode);

            logger.info(String.format(PASS_SET_RES, user.getEmail(), confirmDto.getCode(), "Success"));
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Throwable throwable) {
            if(user != null)
                logger.info(String.format(PASS_SET_RES, user.getEmail(), confirmDto.getCode(), throwable.getMessage()));
            else
                logger.info(String.format(PASS_SET_RES, "", confirmDto.getCode(), throwable.getMessage()));

            return new ResponseEntity<>(new ErrorDto(throwable.getMessage()), HttpStatus.OK);
        }
    }


    private ResponseEntity handleSignup(UserSignupDto userDto) {
        try {
            User user = userService.registerUser(userDto);
            logger.info(String.format(REGISTRATION_RES, userDto.getEmail(), userDto.getPhone(), "Success"));

            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();

            return new ResponseEntity<>(new ErrorDto("SOMETHING_WENT_WRONG"), HttpStatus.OK);
        }
        catch (Throwable throwable) {
            logger.info(String.format(REGISTRATION_RES, userDto.getEmail(), userDto.getPhone(), throwable.getMessage()));

            return new ResponseEntity<>(new ErrorDto(throwable.getMessage()), HttpStatus.OK);
        }
    }
}
