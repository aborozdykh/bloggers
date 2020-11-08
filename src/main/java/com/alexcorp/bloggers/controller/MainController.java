package com.alexcorp.bloggers.controller;

import com.alexcorp.bloggers.domain.User;
import com.alexcorp.bloggers.service.GoogleApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

@Controller
public class MainController {

    @Value("${spring.profiles.active}")
    private String profile;

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


    @Autowired
    private GoogleApiService googleApiService;

    @GetMapping("/")
    String main(@AuthenticationPrincipal User user, Model model){
        HashMap<Object, Object> data = new HashMap<>();

        data.put("profile", user);
        data.put("isDevMode", profile.equals("dev"));
        data.put("google-signin", googleApiService.getLoginUrl());

        model.addAttribute("ServerData", data);

        return "main";
    }

    @GetMapping(value = "/v12/oauth/google")
    String addBlog(@RequestParam String code, Model model) throws IOException {

        HashMap<Object, Object> data = new HashMap<>();
        data.put("url", accessTokenUri);
        data.put("code", code);
        data.put("client_id", clientId);
        data.put("client_secret", clientSecret);
        data.put("redirect_uri", "");
        data.put("grant_type", "authorization_code");

        /*URL url = new URL (accessTokenUri);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        con.setRequestMethod("POST");
        //con.setRequestProperty("Content-Type", "application/json");
        //con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        Map<String, String> arguments = new HashMap<>();
        arguments.put("code", code);
        arguments.put("client_id", clientId);
        arguments.put("client_secret", clientSecret);
        arguments.put("redirect_uri", redirect);
        arguments.put("grant_type", "authorization_code");

        StringJoiner sj = new StringJoiner("&");
        for(Map.Entry<String,String> entry : arguments.entrySet())
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(entry.getValue(), "UTF-8"));
        byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        con.setFixedLengthStreamingMode(length);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        //con.connect();
        try(OutputStream os = con.getOutputStream()) {
            os.write(out);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        System.out.println(response);

        Map<String,Object> result = new ObjectMapper().readValue(response.toString(), HashMap.class);
        String access_token = (String) result.get("access_token");
        String id_token = (String) result.get("id_token");
        System.out.println("AT:" + access_token);*/
        //report(access_token);

        /*String json = "{" +
                "\"code\":\"" + code + "\"," +
                "\"client_id\":\"" + clientId + "\"," +
                "\"client_secret\":\"" + clientSecret + "\"," +
                "\"redirect_uri\":\"" + redirect + "/code\"," +
                "\"grant_type\":\"authorization_code\"" +
            "}";

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }*/

        model.addAttribute("ServerData", data);

        return "add";
    }

    private String report(String accessToken) throws IOException {
        URL url = new URL ("https://www.googleapis.com/youtube/analytics/v1/reports");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Authorization", "Bearer " + accessToken);
        con.setDoOutput(true);

        String json = "{" +
                "\"ids\":\"channel==MINE\"," +
                "\"start-date\":\"2016-05-01\"," +
                "\"end-date\":\"2020-04-10\"," +
                "\"metrics\":\"views\"," +
                "}";
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        System.out.println(response);
        return response.toString();
    }

    @GetMapping(value = "/blogs/save/youtube/code")
    String addBlog(@RequestParam String access_token,
                   @RequestParam String expires_in,
                   @RequestParam String refresh_token,
                   @RequestParam String scope,
                   @RequestParam String token_type) {

        return "main";
    }
}