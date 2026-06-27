package com.mutrix.prepa.application.usecases.cours;

import com.mutrix.prepa.application.dto.commandes.cours.CreateCoursCommand;
import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.domaines.models.Cours;
import com.mutrix.prepa.domaines.interfaces.CoursServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CreateCoursUseCase {

    private final CoursServices coursRepository;

    public CoursResponse execute(CreateCoursCommand command, UUID userId) {
        Cours cours = Cours.builder()
                .title(command.getTitle())
                .body(command.getBody())
                .videoUrl(command.getVideoUrl())
                .matiereId(command.getMatiereId())
                .userId(userId)
                .gratuit(command.getGratuit() != null ? command.getGratuit() : false)
                .isActive(command.getIsActive() != null ? command.getIsActive() : true)
                .metadata(command.getMetadata())
                .build();

        Cours saved = coursRepository.save(cours);
        return CoursResponse.fromDomain(saved);
    }
}