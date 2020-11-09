package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.utils.RequestManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleApiService extends OAuthService{

    @Autowired
    public GoogleApiService(@Value("${server.domain.name}") final String host,
                            @Value("${oauth2.google.client.clientId}") final String clientId,
                            @Value("${oauth2.google.client.clientSecret}") final String clientSecret,
                            @Value("${oauth2.google.client.accessTokenUri}") final String accessTokenUri,
                            @Value("${oauth2.google.client.userAuthorizationUri}") final String userAuthorizationUri,
                            @Value("${oauth2.google.client.scope}") final String scope,
                            @Value("${oauth2.google.client.redirect.signin}") final String redirectSignin,
                            @Value("${oauth2.google.client.redirect.signup}") final String redirectSignup,
                            @Value("${oauth2.google.client.id_token.decoder}") final String idTokenDecoder,
                            RequestManager requestManager) {
        super(host, clientId, clientSecret,
                accessTokenUri, userAuthorizationUri,
                scope, redirectSignin, redirectSignup,
                idTokenDecoder, requestManager);
    }
}
