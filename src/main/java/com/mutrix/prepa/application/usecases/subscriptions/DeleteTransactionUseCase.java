package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase {
    private final TransactionServices transactionServices;

    public void execute(UUID id) {
        transactionServices.delete(id);
    }
}