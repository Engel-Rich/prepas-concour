package com.mutrix.prepa.application.usecases.matieres;

import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.domaines.interfaces.MatiereServices;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class UpdateMatiereUseCase {
    private final MatiereServices matiereServices;

    public Matieres execute(UUID id, String name, String description, Boolean isActive, String logoUrl) {
        final Matieres matieres = matiereServices.getMatiereById(id)
                .orElseThrow(() -> new RuntimeException("Aucune matiere n'existe avec cet ID"));
        if (name        != null && !name.isBlank())        matieres.setName(name);
        if (description != null)                           matieres.setDescription(description);
        if (isActive    != null)                           matieres.setIsActive(isActive);
        if (logoUrl     != null)                           matieres.setLogoUrl(logoUrl);
        return matiereServices.updateMatiere(matieres);
    }

}
