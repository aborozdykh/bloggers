package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;

@Controller
public class MainController {

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    String main(@AuthenticationPrincipal User user, Model model){
        HashMap<Object, Object> data = new HashMap<>();
        data.put("profile", user);
        data.put("isDevMode", profile.equals("dev"));

        model.addAttribute("ServerData", data);

        return "main";
    }

}