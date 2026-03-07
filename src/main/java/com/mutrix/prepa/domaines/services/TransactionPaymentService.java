package com.mutrix.prepa.domaines.services;

import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;

import java.util.UUID;

public interface TransactionPaymentService {

    Transaction initiate(
            UUID paymentServiceId,
            UUID subscriptionId,
            UUID userId,
            double amount,
            TransactionSens sens,
            String phoneNumber
    );

    Transaction verify(String transactionId);
    
}
