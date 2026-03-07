package com.mutrix.prepa.application.dto.mappers;

import com.mutrix.prepa.application.dto.response.ConcourSessionResponse;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;

public final class ConcoursSessionResponseMapper {

    private ConcoursSessionResponseMapper() {
    }

    public static ConcourSessionResponse toDto(
            ConcoursSessions session,
            Concours concours
    ) {

        if (session == null) {
            return null;
        }

        return ConcourSessionResponse.builder()
                .id(session.getId())
                .concours(ConcoursResponseMapper.toDto(concours))
                .name(session.getName())
                .description(session.getDescription())
                .startDate(session.getStartDate())
                .endDate(session.getEndDate())
                .status(session.getStatus())
                .isActive(session.getIsActive())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .metadata(session.getMetadata())
                .build();
    }
}
