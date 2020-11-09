package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.domain.YouTubeChannel;
import com.alexcorp.bloggers.repository.YouTubeChannelRepository;
import com.alexcorp.bloggers.service.GoogleApiService;
import com.alexcorp.bloggers.service.UserService;
import com.alexcorp.bloggers.service.YouTubeApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.alexcorp.bloggers.service.OAuthService.ACCESS_TOKEN;

@Controller
public class GoogleApiRedirectController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String LOGIN_RES = "Sign In(Google) | E-Mail: %s | Status: %s";
    private final static String REGISTRATION_RES = "Sign Up(Google) | E-Mail: %s | Status: %s";

    @Autowired
    private GoogleApiService googleApiService;

    @Autowired
    private YouTubeApiService youTubeApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private YouTubeChannelRepository youTubeRepository;

    @GetMapping(value = "/v1/oauth/google/signin")
    String googleOAuthSignin(@RequestParam String code) throws Throwable {
        Map<String, Object> response = googleApiService.signin(code, true);
        Map<String, Object> profile = googleApiService.deoodeIdToken((String) response.get("id_token"));

        String sub = (String) profile.get("sub"); // user google_id

        User user = userService.loginUser(sub);
        if(user != null) {
            logger.info(String.format(LOGIN_RES, user.getEmail(), "Success"));
            return "redirect:/";
        }

        logger.info(String.format(LOGIN_RES, "", "NOT_FOUND"));
        return "redirect:/signin?error=NOT_FOUND";
    }

    @GetMapping(value = "/v1/oauth/google/signup/{userType}")
    String googleOAuthSignup(@RequestParam String code,
                             @PathVariable String userType) {
        try {
            Map<String, Object> response = googleApiService.signin(code, true);
            Map<String, Object> profile = googleApiService.deoodeIdToken((String) response.get("id_token"));

            User user = userService.registerUser(profile, User.Role.valueOf(userType.toUpperCase()));

            if(user != null) {
                logger.info(String.format(REGISTRATION_RES, user.getEmail(), "Success"));
                return "redirect:/signup/" + userType;
            }

            return "redirect:/signup?error=SOMETHING_WENT_WRONG";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "redirect:/signup?error=SOMETHING_WENT_WRONG";
        }
        catch (Throwable e) {
            logger.info(String.format(REGISTRATION_RES, "", "USER_ALREADY_EXIST"));
            return "redirect:/signup?error=USER_ALREADY_EXIST";
        }
    }

    @GetMapping(value = "/v1/oauth/youtube/signin")
    String youTubeOAuthSignin(@AuthenticationPrincipal User user,
                              @RequestParam String code) throws Throwable {
        Map<String, Object> response = youTubeApiService.signin(code, true);
        YouTubeChannel channel = new YouTubeChannel();
        channel.setAccessToken((String) response.get(ACCESS_TOKEN));
        channel.setRefreshToken((String) response.get("refresh_token"));
        channel.setExpiresIn((Integer) response.get("expires_in"));

        Map<String, Object> channelInfo = youTubeApiService.channelInfo((String) response.get(ACCESS_TOKEN));

        ArrayList<LinkedHashMap> items = (ArrayList<LinkedHashMap>) channelInfo.get("items");

        ArrayList<LinkedHashMap> statistics = (ArrayList<LinkedHashMap>) items.get(0).get(3);

        channel.setSubs((Integer) statistics.get(2).get("subscriberCount"));
        channel.setVideos((Integer) statistics.get(4).get("videoCount"));
        channel.setViews((Integer) statistics.get(0).get("viewCount"));

        channel.setBlogger(user);

        youTubeRepository.save(channel);

        return "redirect:/signup/blogger?" +
                "subs=" + channel.getSubs() +
                "&videos=" + channel.getVideos() +
                "&views=" + channel.getViews() +
                "&allowed=true";
    }
}
