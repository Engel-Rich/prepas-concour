package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
     java.util.Optional<UserEntity> findByFirebaseUid(String firebaseUid);
     java.util.Optional<UserEntity> findByEmail(String email);
     java.util.Optional<UserEntity> findByPhone(String phone);
//     Page<UserEntity> findAll(Pageable pageable);
}
