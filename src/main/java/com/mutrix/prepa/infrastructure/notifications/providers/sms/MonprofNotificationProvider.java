package com.mutrix.prepa.infrastructure.notifications.providers.sms;

import com.mutrix.prepa.domaines.notifications.providers.SmsNotificationProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Fournisseur SMS via l'API LMT Group (https://sms.lmtgroup.com).
 * <p>
 * Variables d'environnement requises :
 * <ul>
 *   <li>{@code MONPROF_SMS_API_KEY}    – clé API (header X-Api-Key)</li>
 *   <li>{@code MONPROF_SMS_API_SECRET} – secret API (header X-Secret)</li>
 *   <li>{@code MONPROF_SMS_SENDER_ID}  – identifiant expéditeur affiché</li>
 * </ul>
 */
@Slf4j
@Component
public class MonprofNotificationProvider implements SmsNotificationProvider {

    private static final String PLACEHOLDER = "placeholder";
    private static final String API_URL = "https://sms.lmtgroup.com/api/v1/pushes";
    private static final String COUNTRY_CODE = "237";

    @Value("${sms.monprof.apiKey:" + PLACEHOLDER + "}")
    private String apiKey;

    @Value("${sms.monprof.apiSecret:" + PLACEHOLDER + "}")
    private String apiSecret;

    @Value("${sms.monprof.senderId:Prepa}")
    private String senderId;

    private final RestTemplate restTemplate = new RestTemplate();
    private boolean configured;

    @PostConstruct
    private void init() {
        configured = !PLACEHOLDER.equals(apiKey) && !PLACEHOLDER.equals(apiSecret);
        if (configured) {
            log.info("Monprof SMS : provider initialisé (senderId={})", senderId);
        } else {
            log.warn("Monprof SMS : MONPROF_SMS_API_KEY / MONPROF_SMS_API_SECRET non configurées — SMS simulés en console.");
        }
    }

    @Override
    public void sendSmsNotification(String phoneNumber, String message) {
        final String msisdn = normalizePhone(phoneNumber);

        if (!configured) {
            log.info("[MONPROF SIMULATION] To: {} | Message: {}", msisdn, message);
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Api-Key", apiKey);
            headers.set("X-Secret", apiSecret);

            Map<String, Object> body = Map.of(
                    "message", message,
                    "senderId", senderId,
                    "msisdn", List.of(msisdn)
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    API_URL, HttpMethod.POST, request, Map.class
            );

            log.info("Monprof SMS : envoyé → status={} | to={}", response.getStatusCode(), msisdn);

        } catch (Exception e) {
            log.error("Monprof SMS : erreur lors de l'envoi à {} : {}", msisdn, e.getMessage(), e);
            throw new RuntimeException("Échec de l'envoi SMS via Monprof : " + e.getMessage(), e);
        }
    }

    /**
     * Normalise le numéro pour l'API LMT :
     * - Supprime le préfixe "+" ou "00"
     * - Supprime l'indicatif pays "237" s'il est déjà présent
     * - Préfixe avec "237"
     * Exemples : "+237612345678" → "237612345678"
     *            "612345678"      → "237612345678"
     *            "237612345678"   → "237612345678"
     */
    private String normalizePhone(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("00")) {
            digits = digits.substring(2);
        }
        if (digits.startsWith(COUNTRY_CODE) && digits.length() > COUNTRY_CODE.length()) {
            return digits;
        }
        return COUNTRY_CODE + digits;
    }
}
