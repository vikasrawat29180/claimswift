package com.claimswift.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration
 * 
 * Configures security settings for the Payment Service
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure security filter chain
     * 
     * For development, security is disabled. In production, implement JWT validation.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> 
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/payments/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll()  // Change to authenticated() in production
                );

        return http.build();
    }
}
