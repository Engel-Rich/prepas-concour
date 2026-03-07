package com.mutrix.prepa.application.usecases.cours;
import com.mutrix.prepa.application.dto.commandes.cours.UpdateCoursCommand;
import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.cors.AccessDeniedException;
import com.mutrix.prepa.cors.EntityNotFoundException;
import com.mutrix.prepa.domaines.models.Cours;
import com.mutrix.prepa.domaines.interfaces.CoursServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCoursUseCase {

    private final CoursServices coursRepository;

    public CoursResponse execute(UUID id, UpdateCoursCommand command, UUID userId) {
        Cours existing = coursRepository.getCoursById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours introuvable avec l'id : " + id));

        if (!existing.getUserId().equals(userId)) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier ce cours");
        }

        Cours updated = Cours.builder()
                .id(existing.getId())
                .title(command.getTitle() != null ? command.getTitle() : existing.getTitle())
                .body(command.getBody() != null ? command.getBody() : existing.getBody())
                .videoUrl(command.getVideoUrl() != null ? command.getVideoUrl() : existing.getVideoUrl())
                .matiereId(command.getMatiereId() != null ? command.getMatiereId() : existing.getMatiereId())
                .userId(existing.getUserId())
                .isActive(command.getIsActive() != null ? command.getIsActive() : existing.getIsActive())
                .metadata(command.getMetadata() != null ? command.getMetadata() : existing.getMetadata())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        Cours saved = coursRepository.save(updated);
        return CoursResponse.fromDomain(saved);
    }
}