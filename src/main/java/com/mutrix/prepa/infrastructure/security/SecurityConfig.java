package com.mutrix.prepa.infrastructure.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@Getter
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // actuator
                        .requestMatchers("/actuator/**").permitAll()
                        // Swagger
                        .requestMatchers(
                                "/api/v1/api-docs/**",
                                "/api/v1/swagger-ui/**",
                                "/api/v1/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // redoc
                        .requestMatchers("/api/v1/redoc/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
