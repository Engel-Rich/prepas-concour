package com.mutrix.prepa.infrastructure.payment.strategies;

import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.services.TransactionPaymentService;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;

import java.util.UUID;

public class MundiPayStrategy implements TransactionPaymentService {

    @Override
    public Transaction initiate(UUID paymentServiceId, UUID subscriptionId, UUID userId, double amount, TransactionSens sens, String phoneNumber) {
        return null;
    }

    @Override
    public Transaction verify(String transactionId) {
        return null;
    }
}
