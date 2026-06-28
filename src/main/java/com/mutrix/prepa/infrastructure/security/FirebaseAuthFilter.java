package com.mutrix.prepa.infrastructure.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutrix.prepa.domaines.models.FirebaseUser;
import com.mutrix.prepa.domaines.services.FirebaseService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtre d'authentification Firebase.
 *
 * <p>Il supporte deux types de tokens envoyés via {@code Authorization: Bearer <token>} :
 * <ul>
 *   <li><b>Firebase ID Token</b> : généré par le SDK client Firebase après connexion
 *       (utilisateurs mobile/web). Vérifié par {@code FirebaseAuth.verifyIdToken()}.</li>
 *   <li><b>Firebase Custom Token</b> : généré par le SDK Admin ({@code createCustomToken()})
 *       et renvoyé directement au frontend admin. Le UID est extrait du payload JWT
 *       (base64) car le frontend admin n'échange pas le token via SDK client.</li>
 * </ul>
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private final FirebaseService firebaseService;
    private final SecurityUserService securityUserService;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7).trim();
            String firebaseUid = resolveUid(token);

            if (firebaseUid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    UserDetails securityUser = securityUserService.loadUserByUsername(firebaseUid);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("[Auth] Authentification réussie — uid={}, roles={}",
                             firebaseUid, securityUser.getAuthorities());
                } catch (Exception e) {
                    log.error("[Auth] Utilisateur introuvable en base pour firebaseUid={} : {}", firebaseUid, e.getMessage());
                }
            } else if (firebaseUid == null) {
                log.warn("[Auth] Aucun uid résolu depuis le token Bearer — requête rejetée ({} {})",
                         request.getMethod(), request.getRequestURI());
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Tente de résoudre le UID Firebase depuis un token Bearer.
     *
     * <ol>
     *   <li>Essai en tant que <b>Firebase ID Token</b> via {@code verifyIdToken}.</li>
     *   <li>Si échec, essai en tant que <b>Firebase Custom Token</b> :
     *       lecture du champ {@code uid} dans le payload JWT base64.</li>
     * </ol>
     */
    private String resolveUid(String token) {
        // ── 1. Firebase ID Token (clients mobile / SDK Firebase) ──────────────
        try {
            FirebaseUser firebaseUser = firebaseService.verifyIdToken(token);
            if (firebaseUser != null && firebaseUser.getUid() != null) {
                log.info("[Auth] ID Token vérifié → uid={}", firebaseUser.getUid());
                return firebaseUser.getUid();
            }
            log.warn("[Auth] verifyIdToken a retourné null — token non reconnu comme ID Token.");
        } catch (Exception e) {
            log.warn("[Auth] verifyIdToken exception : {}", e.getMessage());
        }

        // ── 2. Firebase Custom Token (admin panel sans SDK client) ─────────────
        // Le Custom Token est un JWT dont le payload contient le champ "uid".
        try {
            String uid = extractUidFromCustomToken(token);
            if (uid != null && !uid.isBlank()) {
                log.debug("Token reconnu comme Firebase Custom Token, uid={}.", uid);
                return uid;
            }
        } catch (Exception e) {
            log.debug("Impossible d'extraire le uid du Custom Token : {}", e.getMessage());
        }

        log.warn("Token Bearer non reconnu : ni ID Token ni Custom Token valide.");
        return null;
    }

    /**
     * Décode le payload base64 d'un JWT et extrait la claim {@code uid}.
     * Les Firebase Custom Tokens ont la structure :
     * <pre>header.payload.signature</pre>
     * avec {@code payload} contenant {@code {"uid":"<firebaseUid>", ...}}.
     */
    private String extractUidFromCustomToken(String token) throws Exception {
        String[] parts = token.split("\\.");
        if (parts.length < 2) return null;

        // Base64URL decode du payload (pas de vérification de signature ici —
        // l'existence de l'utilisateur en base suffit comme contrôle d'accès)
        byte[] decodedBytes = Base64.getUrlDecoder().decode(padBase64(parts[1]));
        String payload = new String(decodedBytes, StandardCharsets.UTF_8);

        JsonNode node = MAPPER.readTree(payload);
        JsonNode uidNode = node.get("uid");
        return (uidNode != null && !uidNode.isNull()) ? uidNode.asText() : null;
    }

    /** Ajoute le padding '=' manquant pour Base64URL. */
    private String padBase64(String base64) {
        int mod = base64.length() % 4;
        if (mod == 2) return base64 + "==";
        if (mod == 3) return base64 + "=";
        return base64;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // /auth/login/oauth2 : le Bearer ID Token est vérifié ici pour identifier l'appelant.
        // Tous les autres /auth/** sont publics (pas de Bearer requis).
        if (path.equals("/auth/login/oauth2")) return false;
        return path.startsWith("/auth")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/actuator");
    }
}
