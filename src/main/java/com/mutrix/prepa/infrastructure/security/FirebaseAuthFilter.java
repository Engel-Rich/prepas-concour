package com.mutrix.prepa.infrastructure.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mutrix.prepa.domaines.services.FirebaseService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private FirebaseService firebaseService;
    private SecurityUserService securityUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String firebaseUid = null;
        String firebaseIdToken;

        if (authorizationHeader != null && authorizationHeader.startsWith("Token ")) {
            firebaseIdToken = authorizationHeader.replaceFirst("Token", "").trim();
            try {
                firebaseUid = firebaseService.verifyIdToken(firebaseIdToken).getUid();
            } catch (Exception e) {
                System.out.println("Impossible de recuperer le firebaseUid cause " + e.getClass());
                throw new RuntimeException("Invalid Firebase ID token", e);
            }
            if (firebaseUid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                final UserDetails securityUser = securityUserService.loadUserByUsername(firebaseUid);
                final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        securityUser, null, securityUser.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.startsWith("/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/actuator");

    }
}
