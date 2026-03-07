package com.mutrix.prepa.domaines.interfaces.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TransactionServices {
    
    Transaction getById(UUID id);

    Page<Transaction> list(UUID userId, int page, int size);

    Page<Transaction> list( int page, int size);

    void delete(UUID id);
}
