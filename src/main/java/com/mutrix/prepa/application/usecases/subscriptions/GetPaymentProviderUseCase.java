package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.PaymentProviderResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPaymentProviderUseCase {
    private final PaymentProviderService paymentProviderService;

    public PaymentProviderResponse executeById(UUID id) {
        return PaymentProviderResponse.fromDomain(paymentProviderService.getByID(id));
    }

    public List<PaymentProviderResponse> executeList() {
        return paymentProviderService.list()
                .stream()
                .map(PaymentProviderResponse::fromDomain)
                .toList();
    }
}