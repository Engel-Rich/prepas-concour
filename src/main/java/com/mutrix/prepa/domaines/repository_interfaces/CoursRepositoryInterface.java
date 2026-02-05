package com.mutrix.prepa.domaines.repository_interfaces;

import com.mutrix.prepa.domaines.models.Cours;

import java.util.List;

public interface CoursRepositoryInterface {

    public Cours createCours(Cours cours);

    public Cours getCoursById(String id);

    public Cours updateCours(Cours cours);

    public Iterable<Cours> getAllCours();

    public Iterable<Cours> getAllByMatiereId(String matiereId);


    public void deleteCours(String id);
}
