package com.mutrix.prepa.application.dto.mappers;

import com.mutrix.prepa.application.dto.response.ConcourSessionResponse;
import com.mutrix.prepa.application.dto.response.MatiereSessionDto;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionMatiereEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ConcoursSessionResponseMapper {

    private ConcoursSessionResponseMapper() {
    }

    public static ConcourSessionResponse toDto(
            ConcoursSessions session,
            Concours concours
    ) {
        return toDto(session, concours, Collections.emptyList());
    }

    public static ConcourSessionResponse toDto(
            ConcoursSessions session,
            Concours concours,
            List<ConcoursSessionMatiereEntity> sessionMatieres
    ) {
        if (session == null) {
            return null;
        }

        List<MatiereSessionDto> matieres = sessionMatieres.stream()
                .filter(sm -> sm.getMatiere() != null)
                .map(sm -> MatiereSessionDto.builder()
                        .id(sm.getMatiere().getId())
                        .name(sm.getMatiere().getName())
                        .logoUrl(sm.getMatiere().getLogoUrl())
                        .dureeMinutes(sm.getDureeMinutes())
                        .coefficient(sm.getCoefficient())
                        .build())
                .collect(Collectors.toList());

        return ConcourSessionResponse.builder()
                .id(session.getId())
                .concours(ConcoursResponseMapper.toDto(concours))
                .name(session.getName())
                .description(session.getDescription())
                .amount(session.getAmount())
                .startDate(session.getStartDate())
                .endDate(session.getEndDate())
                .status(session.getStatus())
                .isActive(session.getIsActive())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .metadata(session.getMetadata())
                .matieres(matieres)
                .build();
    }
}
