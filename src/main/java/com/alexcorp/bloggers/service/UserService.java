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
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername(String telNum) throws UsernameNotFoundException {
        User user = userRepository.findByTelNumEndingWith(telNum);

        /*if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }*/
        return user;
    }

    public User loadUserFromSession(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) return loadUserByUsername(auth.getName());

        return null;
    }
}
