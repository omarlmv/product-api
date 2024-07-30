package com.example.productapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.debug("Configuring security filter chain");
        http.authorizeHttpRequests(authorize -> {
            logger.debug("Configuring authorization for /public/**");
            authorize.requestMatchers("/public/**").permitAll()
                    .anyRequest().authenticated();
        }).httpBasic(withDefaults());
        logger.debug("Returning configured SecurityFilterChain");
        return http.build();
    }

    /*@Autowired
    private CustomLoggingFilter customLoggingFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.debug("Configuring security filter chain");
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                //.addFilterBefore(customLoggingFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());
        return http.build();
    }*/

    @Bean
    public UserDetailsService userDetailsService() {
        String rawPassword = "password";
        String encodedPassword = passwordEncoder().encode(rawPassword);

        System.out.println("Configuring in-memory user details service");
        logger.debug("Configuring in-memory user details service");
        UserDetails user = User.builder()
                .username("user")
                .password(encodedPassword)
                .roles("USER")
                .build();
        System.out.println("In-memory user: " + user);
        logger.debug("In-memory user: {}", user);
        logger.debug("Raw password: " + rawPassword);
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("Configuring password encoder");
        logger.debug("Configuring password encoder");
        return new BCryptPasswordEncoder();
    }
}
