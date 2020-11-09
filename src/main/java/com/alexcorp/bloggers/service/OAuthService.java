package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.domain.User;

public interface OAuthService {

    String getLoginUrl();
    String getRegistrationUrl(User.Role role);

}
