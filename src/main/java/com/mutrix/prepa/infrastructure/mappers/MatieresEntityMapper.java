package com.mutrix.prepa.infrastructure.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutrix.prepa.cors.MetadataMapper;
import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.infrastructure.persistence.entities.MatiereEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public  final class MatieresEntityMapper {
    private static final Logger log = LoggerFactory.getLogger(MatieresEntityMapper.class);

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
