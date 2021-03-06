package com.alexcorp.bloggers.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAutoConfiguration
public class SpringConfig {

    @Configuration
    @Profile("dev")
    @PropertySource("classpath:/properties/dev/spring.properties")
    static class Development {}

    @Configuration
    @Profile("pre-prod")
    @PropertySource({"classpath:/properties/pre-prod/spring.properties"})
    static class PreProduction {}

    @Configuration
    @Profile("prod")
    @PropertySource({"classpath:/properties/prod/spring.properties"})
    static class Production {}
}
