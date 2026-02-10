package com.mutrix.prepa.application.usecases.concours;

import com.mutrix.prepa.application.dto.commandes.CreateConcoursDto;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.repository_interfaces.ConcoursRepositoryInterface;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateConcoursUseCase {
    private final ConcoursRepositoryInterface concoursRepositoryInterface;

    public Concours execute(CreateConcoursDto createConcoursDto){
        final  Concours concours = Concours.
                builder().
                name(createConcoursDto.getName()).
                description(createConcoursDto.getDescription()).
                build();
        return concoursRepositoryInterface.createConcours(concours);
    }
}
