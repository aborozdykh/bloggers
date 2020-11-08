package com.alexcorp.bloggers.utils;

import com.alexcorp.bloggers.model.Request;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class RequestManager {

    public static Request Request(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection)(new URL (url)).openConnection();
        connection.setDoOutput(true);

        return new Request(connection);
    }

}
