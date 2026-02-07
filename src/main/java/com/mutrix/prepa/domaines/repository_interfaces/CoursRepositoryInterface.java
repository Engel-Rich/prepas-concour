package com.mutrix.prepa.domaines.repository_interfaces;

import com.mutrix.prepa.domaines.models.Cours;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoursRepositoryInterface {

    public Cours createCours(Cours cours);

    public Optional<Cours> getCoursById(UUID id);

    public Cours updateCours(Cours cours);

    public Page<Cours> getAllCours(Integer page, Integer size);

    public Page<Cours> getAllByMatiereId(UUID matiereId, Pageable pageable);


    public void deleteCours(UUID id);
}
