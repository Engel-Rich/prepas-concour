package com.mutrix.prepa.infrastructure.configs;

import com.mutrix.prepa.infrastructure.security.FirebaseAuthFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@Getter
@EnableWebSecurity
public class SecurityConfig {
    private final FirebaseAuthFilter firebaseAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        // actuator
                        .requestMatchers("/actuator/**").permitAll()
                        // Swagger
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/redoc/**").permitAll()
                        // redoc
                        .requestMatchers("/redoc/**").permitAll()
                        // redoc
                        .requestMatchers("/concours/**").permitAll()
                        // concours
                        .requestMatchers("/matieres/**").permitAll()
                        // matieres
                        .requestMatchers("/cours/**").permitAll()
                        .requestMatchers("/concours-sessions/**").permitAll()

                        // .requestMatchers("/api/courses/**").hasAnyRole("ADMIN", "TEACHER")
                        .anyRequest().authenticated())
                .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
