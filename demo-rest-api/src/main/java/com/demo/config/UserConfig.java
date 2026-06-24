package com.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        var admin = User.withUsername("admin")
                .password("{noop}admin")
                .roles("ADMIN")
                .build();
        var alice = User.withUsername("alice")
                .password("{noop}alice")
                .roles("USER")
                .build();
        var bob = User.withUsername("bob")
                .password("{noop}bob")
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(admin, alice, bob);
    }
}
