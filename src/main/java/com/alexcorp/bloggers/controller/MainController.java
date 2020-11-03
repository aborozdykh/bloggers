package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Value("${spring.profiles.active}")
    private String profile;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    String main(Model model){
        model.addAttribute("isDevMode", profile.equals("dev"));

        return "main";
    }

}