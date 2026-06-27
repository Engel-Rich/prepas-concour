package com.mutrix.prepa.application.usecases.concours;

import com.mutrix.prepa.application.dto.commandes.concours.CreateConcoursDto;
import com.mutrix.prepa.application.dto.mappers.ConcoursResponseMapper;
import com.mutrix.prepa.application.dto.response.ConcourResponseDTO;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class CreateConcoursUseCase {
    private final ConcoursServices concoursServices;

    public ConcourResponseDTO execute(String name, String description, Boolean isActive, String logoUrl) {
        final Concours concours = Concours.builder()
                .name(name)
                .description(description)
                .logoUrl(logoUrl)
                .isActive(isActive != null ? isActive : true)
                .build();
        return ConcoursResponseMapper.toDto(concoursServices.createConcours(concours));
    }
}
