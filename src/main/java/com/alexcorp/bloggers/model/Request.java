package com.alexcorp.bloggers.model;

import com.alexcorp.bloggers.utils.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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

    public Request header(String name, String value) {
        connection.setRequestProperty(name, value);

        return this;
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
        connection.setRequestProperty("Accept", CONTENT_TYPE_JSON);
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
        Reader streamReader;
        int status = connection.getResponseCode();
        /*if (status > 299) {
            InputStream is = connection.getErrorStream();
            streamReader = new InputStreamReader(is);
        } else {
            streamReader = new InputStreamReader(connection.getInputStream());
        }*/
        streamReader = new InputStreamReader(connection.getInputStream());

        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

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

    public static String get(String url) throws Exception {

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet request = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(request)) {

            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                String result = EntityUtils.toString(entity);
                System.out.println(result);
                return result;
            }

        }

        return "";
    }
}
