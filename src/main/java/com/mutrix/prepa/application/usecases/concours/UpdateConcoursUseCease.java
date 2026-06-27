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

    public ConcourResponseDTO execute(UUID id, String name, String description, Boolean isActive, String logoUrl) {
        final Concours concours = concoursServices.getConcoursById(id)
                .orElseThrow(() -> new RuntimeException("Concours introuvable : " + id));
        if (name        != null && !name.isBlank())        concours.setName(name);
        if (description != null)                           concours.setDescription(description);
        if (isActive    != null)                           concours.setIsActive(isActive);
        if (logoUrl     != null)                           concours.setLogoUrl(logoUrl);
        return ConcoursResponseMapper.toDto(concoursServices.updateConcours(concours));
    }
}
