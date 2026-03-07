package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.OtpSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpSessionRepository extends JpaRepository<OtpSessionEntity, UUID> {

    Optional<OtpSessionEntity> findByEmail(String email);

    Optional<OtpSessionEntity> findByPhone(String phone);

}
