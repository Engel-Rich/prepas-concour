package com.mutrix.prepa.application.usecases.concours;

import com.mutrix.prepa.application.dto.mappers.ConcoursResponseMapper;
import com.mutrix.prepa.application.dto.mappers.ConcoursSessionResponseMapper;
import com.mutrix.prepa.application.dto.response.ConcourResponseDTO;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionMatiereRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionMatiereEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

@UseCase
@RequiredArgsConstructor
public class GetConcoursUseCases {

    private final ConcoursServices concoursServices;
    private final ConcoursSessionServices concoursSessionServices;
    private final ConcoursSessionMatiereRepository sessionMatiereRepository;

    public ConcourResponseDTO getById(String id){
        final UUID uuid = UUID.fromString(id);
        final Concours concours = concoursServices.getConcoursById(uuid)
                .orElseThrow(() -> new RuntimeException("Concour not found with this id"));
        ConcourResponseDTO dto = ConcoursResponseMapper.toDto(concours);
        concoursSessionServices.getActiveSessionByConcoursId(uuid)
                .ifPresent(session -> {
                    List<ConcoursSessionMatiereEntity> matieres =
                            sessionMatiereRepository.findBySession_Id(session.getId());
                    dto.setActiveSession(
                            ConcoursSessionResponseMapper.toDto(session, null, matieres)
                    );
                });
        return dto;
    }

    public Page<ConcourResponseDTO> list(Integer page, Integer size){
        Page<Concours> concoursPage = concoursServices.getAllConcours(page, size);
        return concoursPage.map(ConcoursResponseMapper::toDto);
    }
}
