package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.model.Request;
import com.alexcorp.bloggers.utils.RequestManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleApiService implements OAuthService {

    public final static String SCOPE_OPENID = "openid";
    public final static String SCOPE_EMAIL = "email";
    public final static String SCOPE_PROFILE = "profile";

    @Value("${server.domain.name}")
    private String host;

    @Value("${oauth2.google.client.clientId}")
    private String clientId;

    @Value("${oauth2.google.client.clientSecret}")
    private String clientSecret;

    @Value("${oauth2.google.client.accessTokenUri}")
    private String accessTokenUri;

    @Value("${oauth2.google.client.userAuthorizationUri}")
    private String userAuthorizationUri;

    @Value("${oauth2.google.client.scope}")
    private String scope;

    @Value("${oauth2.google.client.redirect}")
    private String redirect;

    @Value("${oauth2.google.client.id_token.decoder}")
    private String idTokenDecoder;

    @Autowired
    private RequestManager requestManager;

    public Map<String, Object> signin(String code) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", host + redirect);
        params.put("grant_type", "authorization_code");

        String idToken = (String) RequestManager
                                    .Request(accessTokenUri)
                                    .Post(params, Request.CONTENT_TYPE_URL_ENCODED)
                                    .then(response -> response)
                                    .get("id_token");

        return deoodeIdToken(idToken);
    }

    public Map<String, Object> deoodeIdToken(String idToken) throws IOException {
        return RequestManager
                    .Request(idTokenDecoder + "?id_token=" + idToken)
                    .Get()
                    .then(response -> response);
    }

    @Override
    public String getLoginUrl() {
        return userAuthorizationUri + "?" +
                "scope=" + scope + "&" +
                "access_type=offline&" +
                "include_granted_scopes=true&" +
                //" state=state_parameter_passthrough_value&" +
                "redirect_uri=" + host + redirect + "&" +
                "response_type=code&" +
                "client_id=" + clientId;
    }
}
