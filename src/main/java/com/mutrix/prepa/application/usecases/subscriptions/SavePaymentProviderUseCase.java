package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.commandes.subscription.CreatePaymentProviderCommand;
import com.mutrix.prepa.application.dto.response.subscription.PaymentProviderResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentProviderService;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavePaymentProviderUseCase {
    private final PaymentProviderService paymentProviderService;

    public PaymentProviderResponse execute(CreatePaymentProviderCommand dto) {
        PaymentProvider provider = PaymentProvider.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .logoUrl(dto.getLogoUrl())
                .build();
        return PaymentProviderResponse.fromDomain(paymentProviderService.save(provider));
    }
}