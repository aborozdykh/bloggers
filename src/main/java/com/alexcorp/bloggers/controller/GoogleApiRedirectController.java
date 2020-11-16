package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.domain.YouTubeChannel;
import com.alexcorp.bloggers.dto.ErrorDto;
import com.alexcorp.bloggers.dto.YouTubeConnectionResultDto;
import com.alexcorp.bloggers.model.ChannelInfo;
import com.alexcorp.bloggers.repository.YouTubeChannelRepository;
import com.alexcorp.bloggers.service.GoogleApiService;
import com.alexcorp.bloggers.service.UserService;
import com.alexcorp.bloggers.service.YouTubeApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.alexcorp.bloggers.service.OAuthService.ACCESS_TOKEN;

@RestController
public class GoogleApiRedirectController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String LOGIN_RES = "Sign In(Google) | E-Mail: %s | Status: %s";
    private final static String REGISTRATION_RES = "Sign Up(Google) | E-Mail: %s | Status: %s";
    private final static String YOUTUBE_CHANNEL_CONNECTED = "You Tube Connect | E-Mail: %s, Channel: %s | Status: %s";

    @Autowired
    private GoogleApiService googleApiService;

    @Autowired
    private YouTubeApiService youTubeApiService;

    @Autowired
    private UserService userService;

    @Autowired
    private YouTubeChannelRepository youTubeRepository;

    @GetMapping(value = "/v1/oauth/google/signin")
    ResponseEntity googleOAuthSignin(@RequestParam String code) throws Throwable {
        Map<String, Object> response = googleApiService.signin(code, true);
        Map<String, Object> profile = googleApiService.deoodeIdToken((String) response.get("id_token"));

        String sub = (String) profile.get("sub"); // user google_id

        User user = userService.loginUser(sub);
        if(user != null) {
            logger.info(String.format(LOGIN_RES, user.getEmail(), "Success"));
            return new ResponseEntity(HttpStatus.OK);
        }

        logger.info(String.format(LOGIN_RES, "", "NOT_FOUND"));
        return new ResponseEntity<> (new ErrorDto("NOT_FOUND"), HttpStatus.OK);
    }

    @GetMapping(value = "/v1/oauth/google/signup/{userType}")
    ResponseEntity googleOAuthSignup(@RequestParam String code,
                             @PathVariable String userType) {
        try {
            Map<String, Object> response = googleApiService.signin(code, true);
            Map<String, Object> profile = googleApiService.deoodeIdToken((String) response.get("id_token"));

            User user = userService.registerUser(profile, User.Role.valueOf(userType.toUpperCase()));

            if(user != null) {
                logger.info(String.format(REGISTRATION_RES, user.getEmail(), "Success"));
                return new ResponseEntity(HttpStatus.OK);
            }

            return new ResponseEntity<> (new ErrorDto("SOMETHING_WENT_WRONG"), HttpStatus.OK);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<> (new ErrorDto("SOMETHING_WENT_WRONG"), HttpStatus.OK);
        }
        catch (Throwable e) {
            logger.info(String.format(REGISTRATION_RES, "", "USER_ALREADY_EXIST"));
            return new ResponseEntity<> (new ErrorDto("USER_ALREADY_EXIST"), HttpStatus.OK);
        }
    }

    @GetMapping(value = "/v1/oauth/youtube/signin")
    ResponseEntity youTubeOAuthSignin(@AuthenticationPrincipal User user,
                                      @RequestParam String code) throws Throwable {
        Map<String, Object> response = youTubeApiService.signin(code, true);

        ChannelInfo channelInfo = youTubeApiService.channelInfo((String) response.get(ACCESS_TOKEN));

        YouTubeConnectionResultDto connectionResult = youTubeApiService.validateChannelConnection(channelInfo);

        if(connectionResult.getAllowed()) {
            YouTubeChannel channel = new YouTubeChannel();
            channel.setAccessToken((String) response.get(ACCESS_TOKEN));
            channel.setRefreshToken((String) response.get("refresh_token"));
            channel.setExpiresIn((Integer) response.get("expires_in"));

            channel.setSubs(connectionResult.getChannelSubscriberCount());
            channel.setViews(connectionResult.getChannelViewsCount());
            channel.setViews(connectionResult.getChannelViewsCount());

            channel.setBlogger(user);

            youTubeRepository.save(channel);
        }

        logger.info(String.format(YOUTUBE_CHANNEL_CONNECTED, user.getEmail(), "", "Success"));
        return new ResponseEntity<> (connectionResult, HttpStatus.OK);
    }
}
