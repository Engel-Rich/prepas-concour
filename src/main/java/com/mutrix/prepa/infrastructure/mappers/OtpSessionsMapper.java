package com.mutrix.prepa.infrastructure.mappers;

import com.mutrix.prepa.cors.MetadataMapper;
import com.mutrix.prepa.domaines.models.OtpSession;
import com.mutrix.prepa.infrastructure.persistence.entities.OtpSessionEntity;

import java.util.Map;


public final class OtpSessionsMapper {
    private OtpSessionsMapper() {
    }

   public static OtpSession toDomain(OtpSessionEntity entity){
        if (entity == null) return null;
        final Map<String, Object> metadata = MetadataMapper.mapFromJsonToMap(entity.getMetadata());
        return OtpSession.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .phone(entity.getPhone())
                .otpHash(entity.getOtpHash())
                .expiresAt(entity.getExpiresAt())
                .metadata(metadata)
                .otpType(entity.getOtpType())
                .build();
    };

  public   static OtpSessionEntity toEntity(OtpSession domain){
        if (domain == null) return null;
        final String metadata = MetadataMapper.mapToJson(domain.getMetadata());
        return OtpSessionEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .fullName(domain.getFullName())
                .phone(domain.getPhone())
                .otpHash(domain.getOtpHash())
                .expiresAt(domain.getExpiresAt())
                .otpType(domain.getOtpType())
                .metadata(metadata)
                .build();
    };
}
