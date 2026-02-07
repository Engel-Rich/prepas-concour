package com.mutrix.prepa.domaines.repository_interfaces;

import com.mutrix.prepa.domaines.models.ConcoursSessions;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface ConcoursSessionRepositoryInterface {

    ConcoursSessions createConcoursSession(ConcoursSessions concoursSession);

    Optional<ConcoursSessions> getConcoursSessionById(UUID id);

    ConcoursSessions updateConcoursSession(ConcoursSessions concoursSession);

    Page<ConcoursSessions> getAllConcoursSessions(Integer page, Integer size);

    Page<ConcoursSessions> getAllByConcoursId(UUID concoursId, Integer page, Integer size);

    void deleteConcoursSession(UUID id);
}
