package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.models.Cours;
import com.mutrix.prepa.domaines.interfaces.CoursServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCoursUseCase {

    private final CoursServices coursRepository;

    public CoursResponse execute(UUID id) {
        Cours cours = coursRepository.getCoursById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable avec l'id : " + id));
        return CoursResponse.fromDomain(cours);
    }
}