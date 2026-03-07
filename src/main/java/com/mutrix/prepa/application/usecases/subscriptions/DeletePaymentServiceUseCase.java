package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletePaymentServiceUseCase {
    private final PaymentServiceService paymentServiceService;
    public void execute(UUID id) { paymentServiceService.delete(id); }
}