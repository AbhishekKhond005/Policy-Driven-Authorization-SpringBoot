package com.policy.engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           PolicyAuthorizationManager authzManager) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                .anyRequest().access(authzManager)
            )
            .csrf(csrf -> csrf.disable())
            .httpBasic(httpBasic -> httpBasic.init(http));

        return http.build();
    }
}
