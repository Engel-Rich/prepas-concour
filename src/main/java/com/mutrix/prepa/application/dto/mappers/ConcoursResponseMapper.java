package com.mutrix.prepa.application.dto.mappers;

import com.mutrix.prepa.application.dto.response.ConcourResponseDTO;
import com.mutrix.prepa.domaines.models.Concours;

public final  class ConcoursResponseMapper {

    private ConcoursResponseMapper() {
    }

    public static ConcourResponseDTO toDto(Concours concours) {

        if (concours == null) {
            return null;
        }

        return ConcourResponseDTO.builder()
                .id(concours.getId())
                .name(concours.getName())
                .description(concours.getDescription())
                .logoUrl(concours.getLogoUrl())
                .isActive(concours.getIsActive())
                .createdAt(concours.getCreatedAt())
                .updatedAt(concours.getUpdatedAt())
                .metadata(concours.getMetadata())
                .build();
    }
}
