package com.mutrix.prepa.application.usecases.matieres;

import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.interfaces.MatiereServices;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class DeleteMatiereUseCase {
    private final MatiereServices matiereServices;
    public void execute(UUID id) {
        matiereServices.deleteMatiere(id);
    }

}
