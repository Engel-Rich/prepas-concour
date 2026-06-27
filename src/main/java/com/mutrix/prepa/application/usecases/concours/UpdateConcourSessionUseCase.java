package com.mutrix.prepa.application.usecases.concours;

import com.mutrix.prepa.application.dto.commandes.concours.UpdateConcoursSessionDto;
import com.mutrix.prepa.application.dto.mappers.ConcoursSessionResponseMapper;
import com.mutrix.prepa.application.dto.response.ConcourSessionResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@RequiredArgsConstructor
@UseCase
public class UpdateConcourSessionUseCase {

    private final ConcoursSessionServices concoursSessionServices;
    private final ConcoursServices concoursServices;

    public ConcourSessionResponse execute(UUID id, UpdateConcoursSessionDto dto) {
        final ConcoursSessions sessions = concoursSessionServices.getConcoursSessionById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with this id"));
        LocalDate startDate = dto.getStartDate() == null ? sessions.getStartDate() : dto.getStartDate();
        LocalDate endDate = dto.getEndDate() == null ? sessions.getEndDate() : dto.getEndDate();

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate))
                throw new RuntimeException("Invalid dates range");
            if (startDate.isBefore(LocalDate.now()))
                throw new RuntimeException("Invalid start dates range");
        }

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            sessions.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            sessions.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            sessions.setStatus(dto.getStatus());
        }
        if (dto.getStartDate() != null) {
            sessions.setStartDate(dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            sessions.setEndDate(dto.getEndDate());
        }
        if (dto.getMetadata() != null && !dto.getMetadata().isEmpty()) {
            sessions.setMetadata(dto.getMetadata());
        }
        if (dto.getIsActive() != null) {
            sessions.setIsActive(dto.getIsActive());
        }
        if (dto.getAmount() != null) {
            sessions.setAmount(dto.getAmount());
        }
        ConcoursSessions sessions1 = concoursSessionServices.updateConcoursSession(sessions);
        final Concours concours = concoursServices.getConcoursById(sessions1.getConcoursId())
                .orElseThrow(() -> new RuntimeException("Concour not found with this id"));
        return ConcoursSessionResponseMapper.toDto(sessions1, concours);
    }
}
