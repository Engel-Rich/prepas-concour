package com.mutrix.prepa.domaines.repository_interfaces;

import com.mutrix.prepa.domaines.models.Concours;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface ConcoursRepositoryInterface {

    Concours createConcours(Concours concours);

    Optional<Concours> getConcoursById(UUID id);

    Concours updateConcours(Concours concours);

    Page<Concours> getAllConcours(Integer page, Integer size);

    void deleteConcours(String id);
}
