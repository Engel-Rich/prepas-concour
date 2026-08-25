package com.mutrix.prepa.infrastructure.configs;

import com.mutrix.prepa.infrastructure.security.DeviceBindingFilter;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.http.HttpMethod;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Getter
@EnableWebSecurity
public class SecurityConfig {
    private final FirebaseAuthFilter firebaseAuthFilter;
    private final DeviceBindingFilter deviceBindingFilter;


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "https://console.prepa.mutrix.org",
                "https://*.mutrix.org",
                "https://*.prepa.mutrix.org"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Preflights CORS — Spring Security doit les laisser passer
                        // avant que le CORS filter réponde avec les bons headers
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        // webhooks fournisseurs de paiement — sécurisés par signature interne
                        .requestMatchers("/webhooks/**").permitAll()
                        // Prévisualisation vidéo : autorisée par jeton signé
                        // à durée de vie courte, pas par le Bearer habituel —
                        // une balise <video> ne peut pas porter d'en-tête.
                        .requestMatchers("/cours-preview/**").permitAll()
                        // actuator
                        .requestMatchers("/actuator/**").permitAll()
                        // Swagger
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/redoc/**").permitAll()
                        // routes publiques
//                        .requestMatchers("/concours/**").permitAll()
//                        .requestMatchers("/matieres/**").permitAll()
//                        .requestMatchers("/cours/**").permitAll()
//                        .requestMatchers("/concours-sessions/**").permitAll()
                        // routes admin — réservées aux administrateurs
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(firebaseAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Après FirebaseAuthFilter : n'agit que si une authentification
                // a été posée, donc jamais sur les routes publiques.
                .addFilterAfter(deviceBindingFilter, FirebaseAuthFilter.class);

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
