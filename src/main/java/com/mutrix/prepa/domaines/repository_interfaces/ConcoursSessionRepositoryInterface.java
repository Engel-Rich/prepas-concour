package com.mutrix.prepa.domaines.repository_interfaces;

import com.mutrix.prepa.domaines.models.ConcoursSessions;

public interface ConcoursSessionRepositoryInterface {

    ConcoursSessions createConcoursSession(ConcoursSessions concoursSession);

    ConcoursSessions getConcoursSessionById(String id);

    ConcoursSessions updateConcoursSession(ConcoursSessions concoursSession);

    Iterable<ConcoursSessions> getAllConcoursSessions();

    Iterable<ConcoursSessions> getAllByConcoursId(String concoursId);

    void deleteConcoursSession(String id);
}
