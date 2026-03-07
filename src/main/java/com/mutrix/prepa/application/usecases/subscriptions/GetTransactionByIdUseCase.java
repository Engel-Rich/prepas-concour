package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.TransactionResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTransactionByIdUseCase {
    private final TransactionServices transactionServices;

    public TransactionResponse execute(UUID id) {
        return TransactionResponse.fromDomain(transactionServices.getById(id));
    }
}