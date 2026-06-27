package com.mutrix.prepa.infrastructure.payment.strategies;

import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class MundiPayStrategy implements TransactionPaymentService {

    @Override
    public Transaction initiate(UUID paymentServiceId, UUID subscriptionId, UUID userId,
                                 double amount, TransactionSens sens, String phoneNumber) {
        log.warn("MundiPay initiate — non encore implémenté");
        throw new UnsupportedOperationException("MundiPay non encore implémenté");
    }

    @Override
    public Transaction verify(String transactionId) {
        log.warn("MundiPay verify — non encore implémenté");
        throw new UnsupportedOperationException("MundiPay non encore implémenté");
    }
}
