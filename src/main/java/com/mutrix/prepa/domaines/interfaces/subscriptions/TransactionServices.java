package com.mutrix.prepa.domaines.interfaces.subscriptions;

import com.mutrix.prepa.domaines.models.subscriptions.Transaction;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionServices {

    Transaction save(Transaction transaction);

    Transaction update(Transaction transaction);

    Transaction getById(UUID id);

    Optional<Transaction> findByReference(String reference);

    /** Dernière transaction liée à une souscription (triée par createdAt desc). */
    Optional<Transaction> findLatestBySubscriptionId(UUID subscriptionId);

    List<Transaction> findAllPending();

    Page<Transaction> list(UUID userId, int page, int size);

    Page<Transaction> list(int page, int size);

    Page<Transaction> listBySession(UUID sessionId, int page, int size);

    Page<Transaction> listByConcours(UUID concoursId, int page, int size);

    Page<Transaction> listByUser(UUID userId, int page, int size);

    void delete(UUID id);
}
