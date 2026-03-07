package com.mutrix.prepa.infrastructure.payment.strategies;

import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Service
public class CAMPAYStrategy implements TransactionPaymentService {
    @Value("${payment.campay.username}")
    private final String campayUsername;

    @Value("${payment.campay.password}")
    private final  String campayPassword;

    @Qualifier("campayRestClient")
    private final RestClient restClient;

    public CAMPAYStrategy(String campayUsername, String campayPassword, RestClient restClient) {
        this.campayUsername = campayUsername;
        this.campayPassword = campayPassword;
        this.restClient = restClient;
    }

    private String getCampayToken(){
        Map<String, String> authTokenRequest  = restClient

                .get().uri(
                uriBuilder -> uriBuilder.path("/token").build()
        ).retrieve().body(new ParameterizedTypeReference<Map<String, String>>() {});
        assert authTokenRequest !=null : new RuntimeException("Unable to get auth token from provider");
        return  "Bearer"+ authTokenRequest.get("token");
    }

    @Override
    public Transaction initiate(UUID paymentServiceId, UUID subscriptionId, UUID userId, double amount, TransactionSens sens, String phoneNumber) {
        return null;
    }

    @Override
    public Transaction verify(String transactionId) {
        return null;
    }
}
