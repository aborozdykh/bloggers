package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.InstagramAccount;
import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.dto.ErrorDto;
import com.alexcorp.bloggers.dto.InstagramConnectionResultDto;
import com.alexcorp.bloggers.model.InstAccInfo;
import com.alexcorp.bloggers.model.Request;
import com.alexcorp.bloggers.service.GoogleApiService;
import com.alexcorp.bloggers.service.InstagramApiService;
import com.alexcorp.bloggers.service.UserService;
import com.alexcorp.bloggers.service.YouTubeApiService;
import com.alexcorp.bloggers.utils.RequestManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class FacebookApiRedirectController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String LOGIN_RES = "Sign In(Google) | E-Mail: %s | Status: %s";
    private final static String ACCOUNT_CONNECTED = "Instagram acc Connect | E-Mail: %s, username: @%s | Status: %s";

    @Autowired
    private GoogleApiService googleApiService;

    @Autowired
    private YouTubeApiService youTubeApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private InstagramApiService instagramApiService;


    @GetMapping(value = "/oauth/inst/signin")
    ResponseEntity instOAuthSignin(@AuthenticationPrincipal User user,
                                   @RequestParam String code) throws Throwable {
        Map<String, Object> response = instagramApiService.signin(code, true);

        String accessToken= (String) response.get("access_token");

        Map<String, Object> response2 = new ObjectMapper()
                .readValue(Request.get("https://graph.facebook.com/v9.0/me/accounts?access_token=" + accessToken), HashMap.class);

        ArrayList<LinkedHashMap> data = (ArrayList<LinkedHashMap>) response2.get("data");

        String pageId = (String) data.get(0).get("id");


        Map<String, Object> response3 = new ObjectMapper()
                .readValue(Request.get("https://graph.facebook.com/v9.0/" + pageId +
                        "?fields=instagram_business_account&access_token=" + accessToken), HashMap.class);

        LinkedHashMap instBusiAccInfo = (LinkedHashMap) response3.get("instagram_business_account");
        if(instBusiAccInfo == null) {
            return new ResponseEntity<> (new ErrorDto("NO_INST_BUSI_ACC_CONNECTED"), HttpStatus.OK);
        }

        String instBusiAccId = (String) instBusiAccInfo.get("id");


        Map<String, Object> response4 = new ObjectMapper()
                .readValue(Request.get("https://graph.facebook.com/v9.0/" + instBusiAccId +
                        "?fields=followers_count,media_count&access_token=" + accessToken), HashMap.class);

        InstAccInfo accInfo = new InstAccInfo();
        accInfo.setFollowersCount(Integer.valueOf((String)response4.get("followers_count")));
        accInfo.setMediaCount(Integer.valueOf((String)response4.get("media_count")));

        InstagramConnectionResultDto connectionResult = instagramApiService.validateChannelConnection(accInfo);

        if(connectionResult.getAllowed()) {
            InstagramAccount instAcc = new InstagramAccount();
            instAcc.setAccessToken(accessToken);
            instAcc.setFollowersCount(connectionResult.getAccFollowersCount());
            instAcc.setMediaCount(connectionResult.getAccMediaCount());

            instAcc.setBlogger(user);
        }

        return new ResponseEntity<> (connectionResult, HttpStatus.OK);
    }
}
