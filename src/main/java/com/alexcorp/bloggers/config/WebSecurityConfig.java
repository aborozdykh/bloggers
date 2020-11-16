package com.alexcorp.bloggers.config;

import com.alexcorp.bloggers.security.CustomPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("com.alexcorp.bloggers")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder = new CustomPasswordEncoder();

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http
                /*.requiresChannel()
                    .anyRequest()
                    .requiresSecure()
                .and()*/
                    .antMatcher("/**").authorizeRequests()
                    .antMatchers("/v1/init", "/error", "/v1/logout",
                            "/v1/signin/**", "/v1/signup/**", "/v1/oauth/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/signin")
                    .permitAll()
                .and()
                    .logout().logoutUrl("/v1/logout").logoutSuccessUrl("/").permitAll()
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/")
                    .permitAll()
                .and()
                    .rememberMe()
                    .rememberMeParameter("remember")
                    .key("uniqueAndSecret")
                    .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 days
                .and()
                    .csrf().disable();
    }

   /* @Bean
    public PrincipalExtractor principalExtractor(UserRepository userRepository) {
        return map -> {
            String id = (String) map.get("sub");
            User user = userRepository.findById(id).orElseGet(() -> {
                User newUser = new User();

                newUser.setId(id);
                newUser.setEmail((String) map.get("email"));

                return newUser;
            });

            user.setLastLogin(LocalDateTime.now());

            return userRepository.save(user);
        };
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }*/

}
