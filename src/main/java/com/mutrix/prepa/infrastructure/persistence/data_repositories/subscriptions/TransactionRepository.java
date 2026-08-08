package com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions;


import com.mutrix.prepa.domaines.valueobjects.TransactionStatus;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    Page<TransactionEntity> findAllByUser_Id(UUID userId, Pageable pageable);

    Optional<TransactionEntity> findByReference(String reference);

    // By session (via subscription → sessions)
    Page<TransactionEntity> findAllBySubscription_Sessions_Id(UUID sessionId, Pageable pageable);

    // By concours (via subscription → sessions → concours)
    Page<TransactionEntity> findAllBySubscription_Sessions_Concours_Id(UUID concoursId, Pageable pageable);

    @Query("SELECT t FROM TransactionEntity t JOIN FETCH t.paymentService ps JOIN FETCH ps.provider JOIN FETCH t.subscription sub JOIN FETCH t.user u WHERE t.status = :status")
    List<TransactionEntity> findAllPendingWithProvider(TransactionStatus status);

    /** Dernière transaction liée à une souscription donnée. */
    Optional<TransactionEntity> findTopBySubscription_IdOrderByCreatedAtDesc(UUID subscriptionId);
}
