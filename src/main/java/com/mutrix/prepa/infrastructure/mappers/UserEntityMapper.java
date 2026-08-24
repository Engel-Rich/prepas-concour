package com.mutrix.prepa.infrastructure.mappers;

import com.mutrix.prepa.cors.MetadataMapper;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;

import java.util.Map;

public final class UserEntityMapper {

    private UserEntityMapper() {
        // Private constructor to prevent instantiation
    }

    public static UserModel toDomainModel(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        Map<String, Object> metadata = MetadataMapper.mapFromJsonToMap(userEntity.getMetadata());
        return UserModel.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .phone(userEntity.getPhone())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .isActive(userEntity.getIsActive())
                .roles(
                        userEntity.getRoles().stream().map(RolesEntityMapper::maFromEntity).toList()
                )
                .fcmToken(userEntity.getFcmToken()).firebaseUid(userEntity.getFirebaseUid())
                .hasEmailVerified(userEntity.getHasEmailVerified()).hasPhoneVerified(userEntity.getHasPhoneVerified())
                .lastLogin(userEntity.getLastLogin()).metadata(metadata)
                .deviceId(userEntity.getDeviceId())
                .platform(userEntity.getPlatform())
                .profilePictureUrl(userEntity.getProfilePictureUrl())
                .passwordHash(userEntity.getPasswordHash())
                .build();
    }

    public static UserEntity toEntity(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        String metadata = MetadataMapper.mapToJson(userModel.getMetadata());

        return UserEntity.builder()
                .id(userModel.getId())
                .name(userModel.getName())
                .email(userModel.getEmail())
                .phone(userModel.getPhone())
                .createdAt(userModel.getCreatedAt())
                .updatedAt(userModel.getUpdatedAt())
                .isActive(userModel.getIsActive())
                .roles(
                        userModel.getRoles().stream().map(RolesEntityMapper::mapToEntity).toList()
                )
                .fcmToken(userModel.getFcmToken()).firebaseUid(userModel.getFirebaseUid())
                .hasEmailVerified(userModel.getHasEmailVerified()).hasPhoneVerified(userModel.getHasPhoneVerified())
                .lastLogin(userModel.getLastLogin()).profilePictureUrl(userModel.getProfilePictureUrl())
                .deviceId(userModel.getDeviceId())
                .platform(userModel.getPlatform())
                .passwordHash(userModel.getPasswordHash())
                .metadata(metadata)
                .build();
    }
}
