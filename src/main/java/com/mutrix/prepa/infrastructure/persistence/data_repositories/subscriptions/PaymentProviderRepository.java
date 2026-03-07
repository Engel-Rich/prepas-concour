package com.mutrix.prepa.infrastructure.persistence.data_repositories.subscriptions;

import aj.org.objectweb.asm.commons.Remapper;
import com.mutrix.prepa.infrastructure.persistence.entities.subscriptions.PaymentProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentProviderRepository extends JpaRepository<PaymentProviderEntity, UUID> {

    Optional<PaymentProviderEntity> findByName(String string);
}
