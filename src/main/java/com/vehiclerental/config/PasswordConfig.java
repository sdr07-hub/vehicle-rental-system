package com.vehiclerental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Creates ONE shared PasswordEncoder that every service can use.
 * BCrypt turns a password like "password123" into a long, unreadable hash
 * before it is saved, so real passwords are never stored in the database.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
