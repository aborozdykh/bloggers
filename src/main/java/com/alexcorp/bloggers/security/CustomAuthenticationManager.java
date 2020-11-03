package com.alexcorp.bloggers.security;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    @Autowired
    private UserService usersService;

    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        UserDetails user = usersService.loadUserByUsername(((User)auth.getPrincipal()).getUsername());
        if (user != null) {
            return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        }

        return null;
    }
}
