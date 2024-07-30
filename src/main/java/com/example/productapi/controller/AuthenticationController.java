package com.example.productapi.controller;

import com.example.productapi.api.AuthenticationApi;
import com.example.productapi.config.JwtUtil;
import com.example.productapi.model.AuthenticationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class AuthenticationController implements AuthenticationApi {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Mono<ResponseEntity<String>> authenticateUser(Mono<AuthenticationRequest> authenticationRequestMono, ServerWebExchange exchange) {
        return authenticationRequestMono.flatMap(authenticationRequest -> {
            try {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
                );
            } catch (AuthenticationException e) {
                return Mono.error(new Exception("Incorrect username or password", e));
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails.getUsername());

            return Mono.just(ResponseEntity.ok(jwt));
        });
    }
}