package com.mutrix.prepa.domaines.repository_interfaces;

import com.mutrix.prepa.domaines.models.Matieres;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface MatiereRepositoryInterface {
    public Matieres createMatiere(Matieres matiere);

    public Optional<Matieres> getMatiereById(UUID id);

    public Matieres updateMatiere(Matieres matiere);

    public Page<Matieres> getAllMatieres(Integer page, Integer size);

    public void deleteMatiere(UUID id);
}
