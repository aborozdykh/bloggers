package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.service.GoogleApiService;
import com.alexcorp.bloggers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class GoogleApiRedirectController {

    @Autowired
    private GoogleApiService googleApiService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/v1/oauth/google/signin")
    String googleOAuthSignin(@RequestParam String code) throws Throwable {
        Map<String, Object> profile = googleApiService.signin(code);
        String sub = (String) profile.get("sub"); // user google_id

        User user = userService.loginUser(sub);
        if(user == null) {
            userService.registerUser(profile);
        }

        return "redirect:/";
    }

}
