package com.alexcorp.bloggers.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAutoConfiguration
public class APIConfig {

    @Configuration
    @Profile("dev")
    @PropertySource("classpath:/properties/dev/api.properties")
    static class Development {}

    @Configuration
    @Profile("pre-prod")
    @PropertySource({"classpath:/properties/pre-prod/api.properties"})
    static class PreProduction {}

    @Configuration
    @Profile("prod")
    @PropertySource({"classpath:/properties/prod/api.properties"})
    static class Production {}
}
