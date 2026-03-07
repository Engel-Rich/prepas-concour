package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.PaymentServiceResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentServiceService;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPaymentServiceUseCase {
    private final PaymentServiceService paymentServiceService;

    public PaymentServiceResponse executeById(UUID id) {
        return PaymentServiceResponse.fromDomain(paymentServiceService.getById(id));
    }

    public List<PaymentServiceResponse> executeByProviderId(UUID providerId) {
        return paymentServiceService.getByProviderId(providerId)
                .stream()
                .map(PaymentServiceResponse::fromDomain)
                .toList();
    }

    public List<PaymentServiceResponse> executeBySens(TransactionSens sens) {
        return paymentServiceService.getBySens(sens)
                .stream()
                .map(PaymentServiceResponse::fromDomain)
                .toList();
    }

    public List<PaymentServiceResponse> executeList() {
        return paymentServiceService.list()
                .stream()
                .map(PaymentServiceResponse::fromDomain)
                .toList();
    }
}