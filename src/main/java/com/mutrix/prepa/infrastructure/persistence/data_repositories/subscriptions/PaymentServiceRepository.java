package com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions;

import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.PaymentServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentServiceRepository extends JpaRepository<PaymentServiceEntity, UUID> {
   List<PaymentServiceEntity> findAllBySens(TransactionSens sens);

   List<PaymentServiceEntity> findAllByProvider_id(UUID id);
}
