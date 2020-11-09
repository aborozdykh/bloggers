package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.service.GoogleApiService;
import com.alexcorp.bloggers.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class GoogleApiRedirectController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String LOGIN_RES = "Sign In(Google) | E-Mail: %s | Status: %s";
    private final static String REGISTRATION_RES = "Sign Up(Google) | E-Mail: %s | Status: %s";

    @Autowired
    private GoogleApiService googleApiService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/v1/oauth/google/signin")
    String googleOAuthSignin(@RequestParam String code) throws Throwable {
        Map<String, Object> profile = googleApiService.signin(code, true);
        String sub = (String) profile.get("sub"); // user google_id

        User user = userService.loginUser(sub);
        if(user != null) {
            logger.info(String.format(LOGIN_RES, user.getEmail(), "Success"));
            return "redirect:/";
        }

        logger.info(String.format(LOGIN_RES, "", "NOT_FOUND"));
        return "redirect:/signin?error=NOT_FOUND";
    }

    @GetMapping(value = "/v1/oauth/google/signup/{userType}")
    String googleOAuthSignup(@RequestParam String code,
                             @PathVariable String userType) {
        try {
            Map<String, Object> profile = googleApiService.signin(code, false);

            User user = userService.registerUser(profile, User.Role.valueOf(userType.toUpperCase()));

            if(user != null) {
                logger.info(String.format(REGISTRATION_RES, user.getEmail(), "Success"));
                return "redirect:/signup/" + userType;
            }

            return "redirect:/signup?error=SOMETHING_WENT_WRONG";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "redirect:/signup?error=SOMETHING_WENT_WRONG";
        }
        catch (Throwable e) {
            logger.info(String.format(REGISTRATION_RES, "", "USER_ALREADY_EXIST"));
            return "redirect:/signup?error=USER_ALREADY_EXIST";
        }
    }
}
