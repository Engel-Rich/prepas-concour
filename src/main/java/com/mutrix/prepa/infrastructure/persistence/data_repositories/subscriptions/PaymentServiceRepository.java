package com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions;

import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.PaymentServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentServiceRepository extends JpaRepository<PaymentServiceEntity, UUID> {
    List<PaymentServiceEntity> findAllBySens(TransactionSens sens);

    List<PaymentServiceEntity> findAllByProvider_id(UUID id);

    @Query("SELECT ps FROM PaymentServiceEntity ps JOIN FETCH ps.provider WHERE ps.id = :id")
    Optional<PaymentServiceEntity> findByIdWithProvider(UUID id);
}
