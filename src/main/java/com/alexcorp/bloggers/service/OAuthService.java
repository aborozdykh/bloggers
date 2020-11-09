package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.model.Request;
import com.alexcorp.bloggers.utils.RequestManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OAuthService{

    public final static String ACCESS_TOKEN = "access_token";

    protected String host;
    protected String clientId;
    protected String clientSecret;
    protected String accessTokenUri;
    protected String userAuthorizationUri;
    protected String scope;
    protected String redirectSignin;
    protected String redirectSignup;
    protected String idTokenDecoder;

    protected RequestManager requestManager;

    public OAuthService(String host, String clientId, String clientSecret,
                        String accessTokenUri, String userAuthorizationUri,
                        String scope, String redirectSignin, String redirectSignup,
                        String idTokenDecoder, RequestManager requestManager) {
        this.host = host;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessTokenUri = accessTokenUri;
        this.userAuthorizationUri = userAuthorizationUri;
        this.scope = scope;
        this.redirectSignin = redirectSignin;
        this.redirectSignup = redirectSignup;
        this.idTokenDecoder = idTokenDecoder;
        this.requestManager = requestManager;
    }

    public Map<String, Object> signin(String code, boolean signin) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        if(signin) params.put("redirect_uri", getRedirectSignin());
        else params.put("redirect_uri", getRedirectSignup(User.Role.BLOGGER));
        params.put("grant_type", "authorization_code");

        return RequestManager
                .Request(accessTokenUri)
                .Post(params, Request.CONTENT_TYPE_URL_ENCODED)
                .then(response -> response);
    }

    public Map<String, Object> deoodeIdToken(String idToken) throws IOException {
        return RequestManager
                .Request(idTokenDecoder + "?id_token=" + idToken)
                .Get()
                .then(response -> response);
    }

    public String getLoginUrl() {
        return userAuthorizationUri + "?" +
                "scope=" + scope + "&" +
                "access_type=offline&" +
                "include_granted_scopes=true&" +
                "redirect_uri=" + host + redirectSignin + "&" +
                "response_type=code&" +
                "client_id=" + clientId;
    }

    public String getRegistrationUrl(User.Role role) {
        return userAuthorizationUri + "?" +
                "scope=" + scope + "&" +
                "access_type=offline&" +
                "include_granted_scopes=true&" +
                "redirect_uri=" + getRedirectSignup(role) + "&" +
                "response_type=code&" +
                "client_id=" + clientId;
    }

    public String getRedirectSignup(User.Role role) {
        return host + redirectSignup.replace("{USER_TYPE}", role.name().toLowerCase());
    }

    public String getRedirectSignin() {
        return host + redirectSignin;
    }
}
