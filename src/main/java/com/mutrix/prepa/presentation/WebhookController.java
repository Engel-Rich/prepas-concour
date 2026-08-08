package com.mutrix.prepa.presentation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutrix.prepa.infrastructure.payment.CampayWebhookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Webhook entrant CamPay.
 *
 * Stratégie : répondre 200 immédiatement (avant tout accès en BDD) pour éviter
 * les timeouts et les relivraisons du fournisseur, puis déléguer le traitement
 * à {@link CampayWebhookService} qui s'exécute de façon asynchrone.
 */
@Slf4j
@RestController
@RequestMapping("/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Callbacks entrants des fournisseurs de paiement")
public class WebhookController {

    @Value("${payment.campay.webhook-secret:}")
    private String campayWebhookSecret;

    private final CampayWebhookService campayWebhookService;
    private final ObjectMapper objectMapper;

    /**
     * POST /webhooks/campay
     *
     * 1. Parse le payload
     * 2. Valide la signature HMAC-SHA256 (si le secret est configuré)
     * 3. Répond 200 immédiatement ← CamPay reçoit l'accusé de réception ici
     * 4. Délègue le traitement métier au service @Async
     */
    @PostMapping("/campay")
    public ResponseEntity<Void> handleCampay(@RequestBody String rawBody) {
        try {
            JsonNode payload = objectMapper.readTree(rawBody);
            String signature = payload.path("signature").asText(null);

            log.debug("Webhook CamPay reçu — status={} external_reference={}",
                    payload.path("status").asText(),
                    payload.path("external_reference").asText());

            // Validation de signature avant tout
            if (signature != null && !campayWebhookSecret.isBlank()) {
                if (!validateHmacJwt(signature, campayWebhookSecret)) {
                    log.warn("Signature CamPay invalide — webhook rejeté");
                    return ResponseEntity.badRequest().build();
                }
            }

            // ─── Accusé de réception immédiat ──────────────────────────────
            // Le fournisseur considère le webhook livré dès réception du 200.
            // Tout le reste s'exécute en arrière-plan.
            campayWebhookService.process(payload);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Erreur parsing webhook CamPay: {}", e.getMessage(), e);
            // On renvoie quand même 200 pour éviter les relivraisons en boucle
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Valide la signature HMAC-SHA256 d'un JWT sans dépendance externe.
     * JWT HS256 : base64url(header).base64url(payload).base64url(signature)
     * La signature couvre "header.payload" signée avec la clé secrète.
     */
    private boolean validateHmacJwt(String jwt, String secret) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) return false;

            String signingInput = parts[0] + "." + parts[1];
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expected = mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8));

            String b64 = parts[2];
            int padding = (4 - b64.length() % 4) % 4;
            byte[] actual = Base64.getUrlDecoder().decode(b64 + "=".repeat(padding));

            return MessageDigest.isEqual(expected, actual);
        } catch (Exception e) {
            log.warn("Impossible de valider la signature JWT: {}", e.getMessage());
            return false;
        }
    }
}
