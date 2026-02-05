package com.mutrix.prepa.domaines.repository_interfaces;

import com.mutrix.prepa.domaines.models.Matieres;

public interface MatiereRepositoryInterface {
    public Matieres createMatiere(Matieres matiere);

    public Matieres getMatiereById(String id);

    public Matieres updateMatiere(Matieres matiere);

    public  Iterable<Matieres> getAllMatieres();

    public void deleteMatiere(String id);
}
