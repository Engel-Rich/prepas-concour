package com.mutrix.prepa.application.usecases.concours;

import com.mutrix.prepa.application.dto.commandes.concours.CreateSessionConcoursDto;
import com.mutrix.prepa.application.dto.mappers.ConcoursSessionResponseMapper;
import com.mutrix.prepa.application.dto.response.ConcourSessionResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import com.mutrix.prepa.domaines.valueobjects.ConcoursSessionsStatus;

@RequiredArgsConstructor
@UseCase
public class CreateSessionConcoursUseCase {
    private final ConcoursSessionServices concoursSessionServices;
    private final ConcoursServices concoursServices;

    public ConcourSessionResponse execute(CreateSessionConcoursDto dto) {
        Concours concours = concoursServices.getConcoursById(dto.getConcoursId())
                .orElseThrow(() -> new RuntimeException("Concour not found with this id"));
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            if (dto.getStartDate().isAfter(dto.getEndDate()))
                throw new RuntimeException("Invalid dates range");
            if (dto.getStartDate().isBefore(LocalDate.now()))
                throw new RuntimeException("Invalid start dates range");
        }

        final ConcoursSessions sessions = ConcoursSessions.builder()
                .name(dto.getName())
                .concoursId(concours.getId())
                .metadata(dto.getMetadata())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(ConcoursSessionsStatus.ONGOING)
                .description(dto.getDescription())
                .build();
        ConcoursSessions response = concoursSessionServices.createConcoursSession(sessions);
        return ConcoursSessionResponseMapper.toDto(response, concours);
    }
}
