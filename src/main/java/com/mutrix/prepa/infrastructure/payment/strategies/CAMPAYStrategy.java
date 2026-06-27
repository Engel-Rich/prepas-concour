package com.mutrix.prepa.infrastructure.payment.strategies;

import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CAMPAYStrategy implements TransactionPaymentService {

    @Value("${payment.campay.username}")
    private String campayUsername;

    @Value("${payment.campay.password}")
    private String campayPassword;

    private final RestClient restClient;
    private final TransactionServices transactionServices;

    public CAMPAYStrategy(
            @Qualifier("campayRestClient") RestClient restClient,
            TransactionServices transactionServices) {
        this.restClient = restClient;
        this.transactionServices = transactionServices;
    }

    private String getCampayToken() {
        Map<String, String> body = Map.of("username", campayUsername, "password", campayPassword);
        Map<String, String> response = restClient
                .post()
                .uri("/token/")
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
        if (response == null || !response.containsKey("token")) {
            throw new RuntimeException("Impossible d'obtenir le token CAMPAY");
        }
        return "Token " + response.get("token");
    }

    @Override
    public Transaction initiate(UUID paymentServiceId, UUID subscriptionId, UUID userId,
                                 double amount, TransactionSens sens, String phoneNumber) {
        String reference = UUID.randomUUID().toString();
        String token = getCampayToken();

        Map<String, Object> payload = Map.of(
                "from", phoneNumber,
                "description", "Paiement inscription concours",
                "amount", String.valueOf((long) amount),
                "external_reference", reference
        );

        Transaction transaction = Transaction.builder()
                .reference(reference)
                .amount(amount)
                .subscriptionId(subscriptionId)
                .userId(userId)
                .paymentServiceId(paymentServiceId)
                .status(TransactionStatus.PENDING)
                .sens(sens)
                .phoneNumber(phoneNumber)
                .isActive(true)
                .build();

        try {
            Map<String, Object> response = restClient
                    .post()
                    .uri("/collect/")
                    .header("Authorization", token)
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response != null) {
                Object ussdCode = response.get("ussd_code");
                if (ussdCode != null) {
                    transaction.setPayToken(ussdCode.toString());
                }
                Object extId = response.get("reference");
                if (extId != null) {
                    transaction.setExternalId(extId.toString());
                }
            }
        } catch (RestClientException e) {
            log.error("Erreur CAMPAY collect: {}", e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setRaisonReject("Erreur communication provider: " + e.getMessage());
        }

        return transactionServices.save(transaction);
    }

    @Override
    public Transaction verify(String reference) {
        String token = getCampayToken();

        try {
            Map<String, Object> response = restClient
                    .get()
                    .uri("/transaction/{ref}/", reference)
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (response == null) {
                throw new RuntimeException("Réponse vide de CAMPAY pour la transaction: " + reference);
            }

            String campayStatus = String.valueOf(response.get("status"));
            TransactionStatus status = switch (campayStatus) {
                case "SUCCESSFUL" -> TransactionStatus.SUCCESS;
                case "FAILED" -> TransactionStatus.FAILED;
                default -> TransactionStatus.PENDING;
            };

            // Chercher la transaction existante par référence
            // On construit un objet minimal pour la mise à jour via le scheduler
            return Transaction.builder()
                    .reference(reference)
                    .status(status)
                    .externalId(response.containsKey("operator_reference")
                            ? String.valueOf(response.get("operator_reference")) : null)
                    .raisonReject(status == TransactionStatus.FAILED
                            ? String.valueOf(response.getOrDefault("message", "Paiement refusé")) : null)
                    .build();

        } catch (RestClientException e) {
            log.error("Erreur CAMPAY verify pour ref={}: {}", reference, e.getMessage());
            return Transaction.builder()
                    .reference(reference)
                    .status(TransactionStatus.PENDING)
                    .build();
        }
    }
}
