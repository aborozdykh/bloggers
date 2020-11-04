package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    public User loadUserByUsername(String login) {
        return null;
    }

    public User loadUserFromSession(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return null;
    }
}
