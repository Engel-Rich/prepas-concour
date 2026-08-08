package com.mutrix.prepa.application.usecases.subscriptions;

import com.mutrix.prepa.application.dto.response.subscription.TransactionResponse;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTransactionByIdUseCase {
    private final TransactionServices transactionServices;

    /** Accès utilisateur — vérifie que la transaction appartient à userId. */
    public TransactionResponse execute(UUID id, UUID requestingUserId) {
        Transaction transaction = transactionServices.getById(id);
        if (!transaction.getUserId().equals(requestingUserId)) {
            throw new EntityNotFoundException("Transaction introuvable avec l'id : " + id);
        }
        return TransactionResponse.fromDomain(transaction);
    }

    /** Accès admin — aucune vérification d'ownership. */
    public TransactionResponse execute(UUID id) {
        return TransactionResponse.fromDomain(transactionServices.getById(id));
    }
}