package com.demo1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        var admin = User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();
        var faculty = User.withUsername("professor").password("{noop}prof").roles("FACULTY").build();
        var student = User.withUsername("alice").password("{noop}alice").roles("STUDENT").build();
        return new InMemoryUserDetailsManager(admin, faculty, student);
    }
}
