package com.mutrix.prepa.application.usecases.matieres;

import com.mutrix.prepa.application.dto.commandes.matieres.UpdateMatieresDto;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.domaines.interfaces.MatiereServices;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class UpdateMatiereUseCase {
    private final MatiereServices matiereServices;

    public Matieres execute(UUID id, UpdateMatieresDto dto) {
        final Matieres matieres = matiereServices.getMatiereById(id)
                .orElseThrow(() -> new RuntimeException("Aucune matiere n'existe avec cet ID"));
        if (dto.getName() != null && !dto.getName().isEmpty()) {
            matieres.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isEmpty()) {
            matieres.setDescription(dto.getDescription());
        }
        if(dto.getIsActive()!=null){
            matieres.setIsActive(dto.getIsActive());
        }
        return matiereServices.updateMatiere(matieres);
    }

}
