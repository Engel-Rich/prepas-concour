package com.mutrix.prepa.infrastructure.mappers;

import com.mutrix.prepa.cors.MetadataMapper;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionEntity;

public final class ConcoursSessionEntityMapper {
    private ConcoursSessionEntityMapper() {
        // Private constructor to prevent instantiation
    }

    public  static ConcoursSessionEntity domainToEntity(ConcoursSessions concoursSession, ConcoursEntity concoursEntity){
        if(concoursSession == null){
            return null;
        }
        String metadata = MetadataMapper.mapToJson(concoursSession.getMetadata());

        return ConcoursSessionEntity.builder()
                .id(concoursSession.getId())
                .name(concoursSession.getName())
                .description(concoursSession.getDescription())
                .amount(concoursSession.getAmount())
                .concours(concoursEntity)
                .createdAt(concoursSession.getCreatedAt())
                .updatedAt(concoursSession.getUpdatedAt())
                .isActive(concoursSession.getIsActive())
                .metadata(metadata)
                .startDate(concoursSession.getStartDate())
                .endDate(concoursSession.getEndDate())
                .status(concoursSession.getStatus())
                .build();
    }

    public  static  ConcoursSessions toDomain(ConcoursSessionEntity concoursSessionEntity){
        if(concoursSessionEntity == null){
            return null;
        }
        
        return ConcoursSessions.builder()
                .id(concoursSessionEntity.getId())
                .name(concoursSessionEntity.getName())
                .description(concoursSessionEntity.getDescription())
                .concoursId(concoursSessionEntity.getConcours().getId())
                .createdAt(concoursSessionEntity.getCreatedAt())
                .updatedAt(concoursSessionEntity.getUpdatedAt())
                .isActive(concoursSessionEntity.getIsActive())
                .amount(concoursSessionEntity.getAmount())
                .metadata(MetadataMapper.mapFromJsonToMap(concoursSessionEntity.getMetadata()))
                .startDate(concoursSessionEntity.getStartDate())
                .endDate(concoursSessionEntity.getEndDate())
                .status(concoursSessionEntity.getStatus())
                .build();
    }
}
