package com.mutrix.prepa.application.usecases.matieres;

import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.domaines.interfaces.MatiereServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GetMatiereUseCases {
    private final MatiereServices matiereServices;
    public Page<Matieres> getMatieres(Integer page, Integer size) {
        return  matiereServices.getAllMatieres(page, size);
    }

    public Matieres getMatiereById(UUID id) {
        return matiereServices.getMatiereById(id)
                .orElseThrow(() -> new RuntimeException("Aucune matiere n'existe avec cet ID"));
    }

}
