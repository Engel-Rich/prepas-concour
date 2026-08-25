package com.mutrix.prepa.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.domaines.interfaces.DeviceBindingService;
import com.mutrix.prepa.domaines.valueobjects.DevicePlatform;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contrôle centralisé de l'appareil sur les requêtes authentifiées.
 *
 * <p>Ne s'exécute que lorsqu'une authentification a été posée par
 * {@link FirebaseAuthFilter}. Les endpoints publics (login, register,
 * webhooks, swagger, actuator) ne sont donc jamais impactés : aucune
 * authentification n'y est présente, le filtre passe la main.
 *
 * <p>La console d'administration tourne dans un navigateur et n'a pas
 * d'identifiant d'appareil stable : les requêtes {@code /admin/**} sont
 * exemptées du contrôle.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DeviceBindingFilter extends OncePerRequestFilter {

    public static final String DEVICE_ID_HEADER = "X-Device-Id";
    public static final String PLATFORM_HEADER = "X-Device-Platform";

    private final DeviceBindingService deviceBindingService;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Requête non authentifiée : route publique ou token invalide.
        // Le refus éventuel appartient à Spring Security, pas à ce filtre.
        if (auth == null || !(auth.getPrincipal() instanceof SecurityUser securityUser)) {
            filterChain.doFilter(request, response);
            return;
        }

        // La console d'administration tourne dans un navigateur : aucun
        // identifiant d'appareil stable n'y existe. L'exemption porte sur le
        // RÔLE et non sur le chemin, car la console consomme aussi des routes
        // hors /admin — /payment-services, /payment-providers,
        // /concours-sessions/…/cours — qu'une liste de chemins obligerait à
        // compléter à chaque nouvel appel.
        if (isAdmin(auth)) {
            filterChain.doFilter(request, response);
            return;
        }

        String deviceId = trimToNull(request.getHeader(DEVICE_ID_HEADER));
        DevicePlatform platform = DevicePlatform.fromHeader(request.getHeader(PLATFORM_HEADER));

        if (deviceId == null) {
            log.warn("[Device] En-tête {} absent — {} {}",
                    DEVICE_ID_HEADER, request.getMethod(), request.getRequestURI());
            reject(response, HttpServletResponse.SC_BAD_REQUEST, "MISSING_DEVICE_ID",
                    "L'identifiant d'appareil est requis.");
            return;
        }

        try {
            deviceBindingService.verify(securityUser.getUser(), deviceId, platform);
        } catch (DeviceMismatchException e) {
            SecurityContextHolder.clearContext();
            log.warn("[Device] Refus pour uid={} — appareil {} non lié",
                    securityUser.getUsername(), deviceId);
            reject(response, HttpServletResponse.SC_CONFLICT,
                    DeviceMismatchException.CODE, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    /** Un compte administrateur n'est pas lié à un appareil. */
    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(granted -> "ADMIN".equals(granted.getAuthority()));
    }

    /**
     * Chemins jamais concernés par le contrôle d'appareil.
     *
     * <p>Les routes publiques sont déjà couvertes par l'absence
     * d'authentification ; celles listées ici le sont explicitement parce
     * qu'elles peuvent porter un jeton sans pour autant relever d'une session
     * mobile liée à un appareil.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/admin")
                || path.startsWith("/auth")
                || path.startsWith("/webhooks")
                || path.startsWith("/cours-preview")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/actuator");
    }

    private void reject(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // Le corps reprend ApiResponseFormat et y ajoute `code`, que le client
        // mobile teste pour déclencher une déconnexion complète.
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("statusCode", status);
        body.put("success", false);
        body.put("error", message);
        body.put("code", code);
        body.put("data", null);

        MAPPER.writeValue(response.getOutputStream(), body);
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}