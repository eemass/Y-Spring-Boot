package com.samiul.Y.config;

import com.samiul.Y.repository.NotificationRepository;
import com.samiul.Y.repository.UserRepository;
import com.samiul.Y.security.JwtAuthFilter;
import com.samiul.Y.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/signup", "/api/auth/logout").permitAll()
                        .requestMatchers("/api/**").authenticated()
                )
                .addFilterBefore(
                        new JwtAuthFilter(jwtUtils, userRepository),
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}
