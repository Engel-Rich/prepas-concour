package com.mutrix.prepa.infrastructure.notifications.providers.sms;

import com.mutrix.prepa.domaines.notifications.providers.SmsNotificationProvider;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Component
public class MonprofNotificationProvider implements SmsNotificationProvider {

    private static final String PLACEHOLDER = "placeholder";
    private static final String API_URL = "https://sms.lmtgroup.com/api/v1/pushes";

    @Value("${sms.monprof.apiKey:" + PLACEHOLDER + "}")
    private String apiKey;

    @Value("${sms.monprof.apiSecret:" + PLACEHOLDER + "}")
    private String apiSecret;

    @Value("${sms.monprof.senderId:Prepa}")
    private String senderId;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private boolean configured;

    @PostConstruct
    private void init() {
        configured = !PLACEHOLDER.equals(apiKey.trim()) && !PLACEHOLDER.equals(apiSecret.trim());
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
            throw new RuntimeException("SMS Cant be sent please try another way");
//            log.info("[MONPROF SIMULATION] To: {} | Message: {}", msisdn, message);
//            return;
        }

        String cleanApiKey    = apiKey.trim();
        String cleanApiSecret = apiSecret.trim();

//        log.info("APIK: {} \n API SECRET {}", cleanApiKey, cleanApiSecret);

        String json = String.format(
                "{\"message\":\"%s\",\"senderId\":\"%s\",\"msisdn\":[\"%s\"]}",
                message.replace("\"", "\\\""), senderId, msisdn
        );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("X-Api-Key", cleanApiKey)
                    .header("X-Secret",  cleanApiSecret)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            log.info("Monprof SMS → POST {} | body={}", API_URL, json);

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            log.info("Monprof SMS : réponse → status={} | body={}", response.statusCode(), response.body());

            if (response.statusCode() >= 300) {
                throw new RuntimeException("HTTP " + response.statusCode() + " : " + response.body());
            }

        } catch (Exception e) {
            log.error("Monprof SMS : erreur lors de l'envoi à {} : {}", msisdn, e.getMessage(), e);
            throw new RuntimeException("Échec de l'envoi SMS via Monprof : " + e.getMessage(), e);
        }
    }

    private String normalizePhone(String phone) {
        if (phone == null) return "";
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("00")) digits = digits.substring(2);
        return digits; // MSISDN complet sans le +, ex: "237654321234" ou "33612345678"
    }
}
