package com.mutrix.prepa.application.usecases.concours;

import com.mutrix.prepa.application.dto.commandes.concours.UpdateConcoursDto;
import com.mutrix.prepa.application.dto.mappers.ConcoursResponseMapper;
import com.mutrix.prepa.application.dto.response.ConcourResponseDTO;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@UseCase
public class UpdateConcoursUseCease {
    private final ConcoursServices concoursServices;

    public ConcourResponseDTO execute(UUID id, UpdateConcoursDto dto) {

        final Concours concours = concoursServices.getConcoursById(id)
                .orElseThrow(() -> new RuntimeException("Concour not found with this id"));
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            concours.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            concours.setDescription(dto.getDescription());
        }
        if (dto.getIsActive() !=null) {
            concours.setIsActive(dto.getIsActive());
        }
        if (dto.getMetadata() != null) {
            concours.setMetadata(dto.getMetadata());

        }
        Concours response = concoursServices.updateConcours(concours);
        return ConcoursResponseMapper.toDto(response);
    }
}
