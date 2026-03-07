package com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions;


import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    Page<TransactionEntity> findAllByUser_Id(UUID userId, Pageable pageable);
}
