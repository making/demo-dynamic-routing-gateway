package com.example.demodynamicroutinggateway.management;

import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http.httpBasic()
            .and()
            .authorizeExchange()
            .matchers(EndpointRequest.to("info", "health")).permitAll()
            .matchers(EndpointRequest.toAnyEndpoint()).authenticated()
            .pathMatchers(HttpMethod.POST, "/routes").authenticated()
            .anyExchange().permitAll()
            .and()
            .csrf().disable()
            .build();
    }
}
