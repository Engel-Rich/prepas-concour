package com.mutrix.prepa.domaines.interfaces;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mutrix.prepa.domaines.models.Cours;

public interface CoursServices {

    public Cours save(Cours cours);

    public Optional<Cours> getCoursById(UUID id);

    public Cours updateCours(Cours cours);

    public Page<Cours> getAllCours(Integer page, Integer size);

    public Page<Cours> getAllByMatiereId(UUID matiereId, Pageable pageable);

    public void deleteCours(UUID id);
}
