package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.cors.AccessDeniedException;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.models.Cours;
import com.mutrix.prepa.domaines.interfaces.CoursServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteCoursUseCase {

    private final CoursServices coursRepository;

    public void execute(UUID id, UUID userId) {
        Cours existing = coursRepository.getCoursById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable avec l'id : " + id));

        if (!existing.getUserId().equals(userId)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer ce cours");
        }

        coursRepository.deleteCours(id);
    }
}