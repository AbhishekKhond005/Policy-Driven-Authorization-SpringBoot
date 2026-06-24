package com.demo2.config;

import com.policy.engine.config.PolicyAuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, PolicyAuthorizationManager authzManager) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().access(authzManager)
            )
            .csrf(csrf -> csrf.disable())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
