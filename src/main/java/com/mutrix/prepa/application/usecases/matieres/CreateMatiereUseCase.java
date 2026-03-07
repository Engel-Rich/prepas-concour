package com.mutrix.prepa.application.usecases.matieres;

import com.mutrix.prepa.application.dto.commandes.matieres.CreateMatieresDto;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.domaines.interfaces.MatiereServices;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CreateMatiereUseCase {
    private final MatiereServices matiereServices;

    public Matieres execute(CreateMatieresDto dto) {
        final Matieres matieres = Matieres.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        return matiereServices.createMatiere(matieres);
    }

}
