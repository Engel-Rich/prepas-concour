package com.mutrix.prepa.infrastructure.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutrix.prepa.cors.MetadataMapper;
import com.mutrix.prepa.domaines.models.Users;
import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;


import java.util.Map;

@Slf4j
public final class UserEntityMapper {

    private  UserEntityMapper() {
        // Private constructor to prevent instantiation
    }
 public static Users toDomainModel(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
     Map<String, Object> metadata = MetadataMapper.mapFromJsonToMap(userEntity.getMetadata());
        return Users.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .isActive(userEntity.getIsActive())
                .roles(userEntity.getRoles())
                .fcmToken(userEntity.getFcmToken()).firebaseUid(userEntity.getFirebaseUid())
                .isEmailVerified(userEntity.getIsEmailVerified()).isPhoneVerified(userEntity.getIsPhoneVerified())
                .lastLogin(userEntity.getLastLogin()).metadata(metadata)
                .profilePictureUrl(userEntity.getProfilePictureUrl())
                .passwordHash(userEntity.getPasswordHash())
                .build();
    }

    public static UserEntity toEntity(Users users) {
        if (users == null) {
            return null;
        }
            String metadata =  MetadataMapper.mapToJson(users.getMetadata());

        return UserEntity.builder()
                .id(users.getId())
                .name(users.getName())
                .email(users.getEmail())
                .phone(users.getPhone())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .isActive(users.getIsActive())
                .roles(users.getRoles())
                .fcmToken(users.getFcmToken()).firebaseUid(users.getFirebaseUid())
                .isEmailVerified(users.getIsEmailVerified()).isPhoneVerified(users.getIsPhoneVerified())
                .lastLogin(users.getLastLogin()).profilePictureUrl(users.getProfilePictureUrl())
                .passwordHash(users.getPasswordHash())
                .metadata(metadata)
                .build();
    }
}
