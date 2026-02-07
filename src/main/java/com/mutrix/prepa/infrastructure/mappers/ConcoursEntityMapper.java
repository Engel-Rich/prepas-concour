package com.mutrix.prepa.infrastructure.mappers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mutrix.prepa.cors.MetadataMapper;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public final class ConcoursEntityMapper {

    private static final Logger log = LoggerFactory.getLogger(ConcoursEntityMapper.class);

    private  ConcoursEntityMapper() {
        // Private constructor to prevent instantiation
    }

    public  static ConcoursEntity toConcoursEntity(Concours concours) {
        if (concours == null) {
            return null;
        }
        String metadata = MetadataMapper.mapToJson(concours.getMetadata());

        return ConcoursEntity.builder()
                .id(concours.getId())
                .name(concours.getName())
                .description(concours.getDescription())
                .logoUrl(concours.getLogoUrl())
                .createdAt(concours.getCreatedAt())
                .updatedAt(concours.getUpdatedAt())
                .isActive(concours.getIsActive())
                .metadata(metadata)
                .build();

    }

    public  static  Concours toConcoursDomainModel(ConcoursEntity concoursEntity) {
        if (concoursEntity == null) {
            return null;
        }
        Map<String, Object> metadata = MetadataMapper.mapFromJsonToMap(concoursEntity.getMetadata());
    return  Concours.builder()
            .id(concoursEntity.getId())
            .name(concoursEntity.getName())
            .description(concoursEntity.getDescription())
            .logoUrl(concoursEntity.getLogoUrl())
            .createdAt(concoursEntity.getCreatedAt())
            .updatedAt(concoursEntity.getUpdatedAt())
            .isActive(concoursEntity.getIsActive())
            .metadata(metadata)
            .build();
    }

}
