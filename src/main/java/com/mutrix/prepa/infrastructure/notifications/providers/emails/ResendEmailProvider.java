package com.mutrix.prepa.infrastructure.notifications.providers.emails;

import com.mutrix.prepa.domaines.notifications.providers.EmailNotificationProvider;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fournisseur d'e-mail via le SDK officiel Resend (https://resend.com).
 * <p>
 * Variables d'environnement :
 * <ul>
 *   <li>{@code RESEND_API_KEY} – clé API Resend (ex : {@code re_xxxxxxxxxx})</li>
 *   <li>{@code RESEND_FROM}    – adresse expéditeur (ex : {@code Prepa Concours <noreply@prepa-concours.cm>})</li>
 * </ul>
 */
@Slf4j
@Component
public class ResendEmailProvider implements EmailNotificationProvider {

    private static final String PLACEHOLDER_KEY = "re_placeholder";

    @Value("${mail.resend.apiKey:" + PLACEHOLDER_KEY + "}")
    private String apiKey;

    @Value("${mail.resend.from:Prepa Concours <noreply@prepa-concours.cm>}")
    private String fromAddress;

    private Resend resendClient;

    @PostConstruct
    private void init() {
        if (!PLACEHOLDER_KEY.equals(apiKey)) {
            resendClient = new Resend(apiKey);
            log.info("Resend : client initialisé (from={})", fromAddress);
        } else {
            log.warn("Resend : RESEND_API_KEY non configurée — les e-mails seront simulés en console.");
        }
    }

    @Override
    public void sendEmailNotification(
            List<String> emailAddress,
            String subject,
            String message,
            List<String> ccEmailAddresses) {

        if (emailAddress == null || emailAddress.isEmpty()) {
            log.warn("Resend : aucun destinataire fourni, envoi annulé.");
            return;
        }

        // Mode simulation si la clé API n'est pas configurée
        if (resendClient == null) {
            emailAddress.forEach(addr ->
                    log.info("[RESEND SIMULATION] To: {} | Subject: {}", addr, subject));
            return;
        }

        try {
            CreateEmailOptions.Builder builder = CreateEmailOptions.builder()
                    .from(fromAddress)
                    .to(emailAddress)
                    .subject(subject)
                    .html(message);

            if (ccEmailAddresses != null && !ccEmailAddresses.isEmpty()) {
                builder.cc(ccEmailAddresses);
            }

            CreateEmailResponse response = resendClient.emails().send(builder.build());
            log.info("Resend : e-mail envoyé → id={} | to={}", response.getId(), emailAddress);

        } catch (ResendException e) {
            log.error("Resend : erreur API lors de l'envoi à {} : {}", emailAddress, e.getMessage(), e);
            throw new RuntimeException("Échec de l'envoi via Resend : " + e.getMessage(), e);
        }
    }
}
