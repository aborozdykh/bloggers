package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.utils.RequestManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class YouTubeApiService extends OAuthService{

    @Value("${youtube.api.key}")
    private String apiKey;

    @Autowired
    public YouTubeApiService(@Value("${server.domain.name}") final String host,
                             @Value("${oauth2.youtube.client.clientId}") final String clientId,
                             @Value("${oauth2.youtube.client.clientSecret}") final String clientSecret,
                             @Value("${oauth2.youtube.client.accessTokenUri}") final String accessTokenUri,
                             @Value("${oauth2.youtube.client.userAuthorizationUri}") final String userAuthorizationUri,
                             @Value("${oauth2.youtube.client.scope}") final String scope,
                             @Value("${oauth2.youtube.client.redirect}") final String redirectSignin,
                             @Value("${oauth2.google.client.id_token.decoder}") final String idTokenDecoder,
                             RequestManager requestManager) {
        super(host, clientId, clientSecret,
                accessTokenUri, userAuthorizationUri,
                scope, redirectSignin, redirectSignin,
                idTokenDecoder, requestManager);


    }

    public Map<String, Object> channelInfo(String accessToken) throws Exception {
        /*return RequestManager
                .Request("https://youtube.googleapis.com/youtube/v3/channels?" +
                        "part=" + "statistics&" +
                        "mine=" + "true&" +
                        "key=" + apiKey)
                .header("Authorization", "Bearer " + accessToken)
                .Get()
                .then(response -> response);*/

        return new ObjectMapper().readValue(sendGet(accessToken), HashMap.class);
    }

    private String sendGet(String accessToken) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet("https://youtube.googleapis.com/youtube/v3/channels?" +
                "part=" + "statistics&" +
                "mine=" + "true&" +
                "key=" + apiKey);

        // add request headers
        request.addHeader("Authorization", "Bearer " + accessToken);

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                System.out.println(result);
                return result;
            }

        }

        return "";
    }

    /*@Override
    public Map<String, Object> signin(String code, boolean signin) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("redirect_uri", getRedirectSignin());
        params.put("grant_type", "authorization_code");

        Map<String, Object> response =  RequestManager
                .Request(accessTokenUri)
                .Post(params, Request.CONTENT_TYPE_URL_ENCODED)
                .then(r -> r);

        return response;
    }*/
}
