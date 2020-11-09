package com.alexcorp.bloggers.model;

import com.alexcorp.bloggers.utils.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Request {

    public final static String CONTENT_TYPE_URL_ENCODED = "application/x-www-form-urlencoded; charset=UTF-8";
    public final static String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

    private HttpURLConnection connection;

    public Request(HttpURLConnection connection) {
        this.connection = connection;
    }

    public Request Post(Map<String, Object> params, String contentType) throws IOException {
        String data;
        if(contentType.equals(CONTENT_TYPE_URL_ENCODED)) data = writeUrlEncoded(params);
        else data = writeJson(params);

        byte[] body = data.getBytes(StandardCharsets.UTF_8);

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setFixedLengthStreamingMode(body.length);

        try(OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(body);
        }

        return this;
    }

    public Request Get(Map<String, Object> params) throws IOException {
        String data = writeJson(params);

        byte[] body = data.getBytes(StandardCharsets.UTF_8);

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", CONTENT_TYPE_JSON);
        connection.setFixedLengthStreamingMode(body.length);

        try(OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(body);
        }

        return this;
    }

    public Request Get (String ... params) throws IOException {
        Map<String, Object> data = new HashMap<>();
        for(int i = 0; i < params.length / 2; i++) {
            data.put(params[i * 2], params[i * 2 + 1]);
        }

        return Get(data);
    }

    public Map<String, Object> then(RequestHandler handler) throws IOException {
        //error();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();

        while (reader.ready()) {
            response.append(reader.readLine().trim());
        }

        Map<String, Object> responseJson = new ObjectMapper().readValue(response.toString(), HashMap.class);

        return handler.handle(responseJson);
    }

    public void error() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();

        while (reader.ready()) {
            response.append(reader.readLine().trim());
        }

        System.out.println(response.toString());
    }

    private String writeJson(Map<String, Object> params) {
        return params.keySet().stream()
                .map(key -> key + "=" + params.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String writeUrlEncoded(Map<String, Object> params) throws UnsupportedEncodingException {
        StringJoiner sj = new StringJoiner("&");
        for(Map.Entry<String, Object> entry : params.entrySet())
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                    + URLEncoder.encode((String) entry.getValue(), "UTF-8"));
        return sj.toString();
    }
}
