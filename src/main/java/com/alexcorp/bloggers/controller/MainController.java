package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.service.GoogleApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MainController {

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private GoogleApiService googleApiService;

    @GetMapping("/")
    String main(@AuthenticationPrincipal User user, Model model){
        HashMap<Object, Object> data = new HashMap<>();

        data.put("user", extractUserInfo(user));
        data.put("isDevMode", profile.equals("dev"));
        data.put("google-signin", googleApiService.getLoginUrl());
        data.put("google-signup-blog", googleApiService.getRegistrationUrl(User.Role.BLOGGER));
        data.put("google-signup-busi", googleApiService.getRegistrationUrl(User.Role.BUSINESS));

        model.addAttribute("ServerData", data);

        return "main";
    }

    private Map<String, Object> extractUserInfo(User user) {
        if(user == null) return  null;

        Map<String, Object> info = new HashMap<>();
        info.put("email", user.getEmail());
        info.put("name", user.getName());
        info.put("surname", user.getSurname());
        info.put("roles", user.getRoles());
        info.put("profile", user.getPhone() != null);

        return info;
    }

    @GetMapping("/add")
    String add() {
        return "add";
    }
}