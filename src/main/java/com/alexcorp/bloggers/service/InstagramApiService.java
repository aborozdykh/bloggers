package com.alexcorp.bloggers.service;

import com.alexcorp.bloggers.dto.InstagramConnectionResultDto;
import com.alexcorp.bloggers.dto.YouTubeConnectionResultDto;
import com.alexcorp.bloggers.model.ChannelInfo;
import com.alexcorp.bloggers.model.InstAccInfo;
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
public class InstagramApiService extends OAuthService{

    @Value("${oauth2.inst.client.longAccessTokenUri}")
    private String longAccessTokenUrl;

    @Value("${inst.min.followers}")
    private Integer minFollowersCount;

    @Value("${inst.min.media}")
    private Integer minMediaCount;

    @Autowired
    public InstagramApiService(@Value("${server.domain.name}") final String host,
                               @Value("${oauth2.inst.client.clientId}") final String clientId,
                               @Value("${oauth2.inst.client.clientSecret}") final String clientSecret,
                               @Value("${oauth2.inst.client.accessTokenUri}") final String accessTokenUri,
                               @Value("${oauth2.inst.client.userAuthorizationUri}") final String userAuthorizationUri,
                               @Value("${oauth2.inst.client.scope}") final String scope,
                               @Value("${oauth2.inst.client.redirect}") final String redirectSignin,
                               @Value("${oauth2.inst.client.id_token.decoder}") final String idTokenDecoder,
                               RequestManager requestManager) {
        super(host, clientId, clientSecret,
                accessTokenUri, userAuthorizationUri,
                scope, redirectSignin, redirectSignin,
                idTokenDecoder, requestManager);


    }

    public  Map<String, Object> refresh(String shortLiveAccessToken) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(longAccessTokenUrl + "?" +
                "grant_type=" + "fb_exchange_token" +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&fb_exchange_token=" + shortLiveAccessToken);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                System.out.println(result);
                return new ObjectMapper().readValue(result, HashMap.class);
            }

        }

        return null;
    }

    public InstagramConnectionResultDto validateChannelConnection(InstAccInfo account) {
        InstagramConnectionResultDto result = new InstagramConnectionResultDto();

        result.setMinValues(minFollowersCount, minMediaCount);
        result.setChannelValues(account.getFollowersCount(), account.getMediaCount());

        boolean allowed =
                        account.getFollowersCount() >= minFollowersCount &&
                        account.getMediaCount() >= minMediaCount;

        result.setAllowed(allowed);

        return result;
    }

    @Override
    public String getLoginUrl() {
        return userAuthorizationUri + "?" +
                "redirect_uri=" + getRedirectSignin() + "&" +
                "scope=" + scope + "&" +
                "response_type=code&" +
                "client_id=" + clientId;
    }
}
