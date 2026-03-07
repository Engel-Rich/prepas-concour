package com.mutrix.prepa.infrastructure.mappers;

import java.util.Map;

import com.mutrix.prepa.cors.MetadataMapper;
import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.infrastructure.persistence.entities.MatiereEntity;

public final class MatieresEntityMapper {

    private MatieresEntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Matieres toDomain(MatiereEntity entity) {
        if (entity == null) {
            return null;
        }
        Map<String, Object> metadata = MetadataMapper.mapFromJsonToMap(entity.getMetadata());

        return Matieres.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isActive(entity.getIsActive())
                .logoUrl(entity.getLogoUrl())
                .metadata(metadata)
                .build();
    }

    public static MatiereEntity toEntity(Matieres domain) {
        if (domain == null) {
            return null;
        }
        String metadata = MetadataMapper.mapToJson(domain.getMetadata());

        return MatiereEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .isActive(domain.getIsActive())
                .logoUrl(domain.getLogoUrl())
                .metadata(metadata)
                .build();
    }
}
