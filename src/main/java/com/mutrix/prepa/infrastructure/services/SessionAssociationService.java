package com.mutrix.prepa.infrastructure.services;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.application.dto.response.MatiereResponseDto;
import com.mutrix.prepa.infrastructure.mappers.CoursEntityMapper;
import com.mutrix.prepa.infrastructure.mappers.MatieresEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionCoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionMatiereRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.CoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.MatiereRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionCoursEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionMatiereEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.MatiereEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionAssociationService {

    private final ConcoursSessionRepository sessionRepository;
    private final MatiereRepository matiereRepository;
    private final CoursRepository coursRepository;
    private final ConcoursSessionMatiereRepository sessionMatiereRepository;
    private final ConcoursSessionCoursRepository sessionCoursRepository;

    // ── Matières ──────────────────────────────────────────────────────────────

    @Transactional
    public MatiereResponseDto addMatiereToSession(UUID sessionId, UUID matiereId) {
        if (sessionMatiereRepository.existsBySession_IdAndMatiere_Id(sessionId, matiereId)) {
            throw new IllegalStateException("La matière est déjà associée à cette session");
        }
        ConcoursSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session introuvable : " + sessionId));
        MatiereEntity matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> new RuntimeException("Matière introuvable : " + matiereId));

        sessionMatiereRepository.save(ConcoursSessionMatiereEntity.builder()
                .session(session)
                .matiere(matiere)
                .build());

        return toMatiereDto(matiere);
    }

    @Transactional
    public void removeMatiereFromSession(UUID sessionId, UUID matiereId) {
        if (!sessionMatiereRepository.existsBySession_IdAndMatiere_Id(sessionId, matiereId)) {
            throw new RuntimeException("Association introuvable");
        }
        sessionMatiereRepository.deleteBySession_IdAndMatiere_Id(sessionId, matiereId);
    }

    @Transactional(readOnly = true)
    public List<MatiereResponseDto> getMatieresForSession(UUID sessionId) {
        return sessionMatiereRepository.findBySession_Id(sessionId).stream()
                .map(e -> toMatiereDto(e.getMatiere()))
                .toList();
    }

    // ── Cours ─────────────────────────────────────────────────────────────────

    @Transactional
    public CoursResponse addCoursToSession(UUID sessionId, UUID coursId) {
        if (sessionCoursRepository.existsBySession_IdAndCours_Id(sessionId, coursId)) {
            throw new IllegalStateException("Le cours est déjà associé à cette session");
        }
        ConcoursSessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session introuvable : " + sessionId));
        CoursEntity cours = coursRepository.findById(coursId)
                .orElseThrow(() -> new RuntimeException("Cours introuvable : " + coursId));

        sessionCoursRepository.save(ConcoursSessionCoursEntity.builder()
                .session(session)
                .cours(cours)
                .build());

        return CoursResponse.fromDomain(CoursEntityMapper.toDCoursDomain(cours));
    }

    @Transactional
    public void removeCoursFromSession(UUID sessionId, UUID coursId) {
        if (!sessionCoursRepository.existsBySession_IdAndCours_Id(sessionId, coursId)) {
            throw new RuntimeException("Association introuvable");
        }
        sessionCoursRepository.deleteBySession_IdAndCours_Id(sessionId, coursId);
    }

    @Transactional(readOnly = true)
    public List<CoursResponse> getCoursForSession(UUID sessionId) {
        return sessionCoursRepository.findBySession_Id(sessionId).stream()
                .map(e -> CoursResponse.fromDomain(CoursEntityMapper.toDCoursDomain(e.getCours())))
                .toList();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private MatiereResponseDto toMatiereDto(MatiereEntity entity) {
        var domain = MatieresEntityMapper.toDomain(entity);
        return MatiereResponseDto.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .logoUrl(domain.getLogoUrl())
                .isActive(domain.getIsActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .metadata(domain.getMetadata())
                .build();
    }
}
