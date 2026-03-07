package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.TransactionResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListTransactionsUseCase {
    private final TransactionServices transactionServices;

    public Page<TransactionResponse> execute(UUID userId, int page, int size) {
        return transactionServices.list(userId, page, size)
                .map(TransactionResponse::fromDomain);
    }

    public Page<TransactionResponse> execute(int page, int size) {
        return transactionServices.list(page, size)
                .map(TransactionResponse::fromDomain);
    }
}