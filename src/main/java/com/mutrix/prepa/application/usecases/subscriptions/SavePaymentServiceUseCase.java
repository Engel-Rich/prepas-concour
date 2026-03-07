package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.commandes.subscription.CreatePaymentServiceCommand;
import com.mutrix.prepa.application.dto.response.subscription.PaymentServiceResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentServiceService;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavePaymentServiceUseCase {
    private final PaymentServiceService paymentServiceService;

    public PaymentServiceResponse execute(CreatePaymentServiceCommand dto) {
        PaymentService service = PaymentService.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .rate(dto.getRate())
                .providerRate(dto.getProviderRate())
                .regExp(dto.getRegExp())
                .logoUrl(dto.getLogoUrl())
                .sens(dto.getSens())
                .paymentProviderId(dto.getPaymentProviderId())
                .build();
        return PaymentServiceResponse.fromDomain(paymentServiceService.save(service));
    }
}