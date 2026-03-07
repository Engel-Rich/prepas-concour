package com.mutrix.prepa.domaines.interfaces;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.mutrix.prepa.domaines.models.Matieres;

public interface MatiereServices {
    public Matieres createMatiere(Matieres matiere);

    public Optional<Matieres> getMatiereById(UUID id);

    public Matieres updateMatiere(Matieres matiere);

    public Page<Matieres> getAllMatieres(Integer page, Integer size);

    public void deleteMatiere(UUID id);
}
