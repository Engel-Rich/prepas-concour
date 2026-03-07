package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletePaymentProviderUseCase {
    private final PaymentProviderService paymentProviderService;
    public void execute(UUID id) { paymentProviderService.delete(id); }
}
