package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    java.util.Optional<UserEntity> findByFirebaseUid(String firebaseUid);
    java.util.Optional<UserEntity> findByEmail(String email);
    java.util.Optional<UserEntity> findByPhone(String phone);
    Page<UserEntity> findAll(Pageable pageable);
    long countByIsActive(Boolean isActive);
    long countByCreatedAtAfter(LocalDateTime date);
    long countByHasEmailVerified(Boolean hasEmailVerified);
    long countByHasPhoneVerified(Boolean hasPhoneVerified);

    @Query("SELECT u FROM UserEntity u JOIN u.roles r WHERE r.id = :roleId")
    Page<UserEntity> findByRoleId(@Param("roleId") UUID roleId, Pageable pageable);

    @Query("SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE r.id = :roleId")
    long countByRoleId(@Param("roleId") UUID roleId);
}
