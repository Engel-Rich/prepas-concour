package com.mutrix.prepa.application.usecases.matieres;

import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.domaines.interfaces.MatiereServices;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class CreateMatiereUseCase {
    private final MatiereServices matiereServices;

    public Matieres execute(String name, String description, Boolean isActive, String logoUrl) {
        final Matieres matieres = Matieres.builder()
                .name(name)
                .description(description)
                .isActive(isActive != null ? isActive : true)
                .logoUrl(logoUrl)
                .build();
        return matiereServices.createMatiere(matieres);
    }

}
