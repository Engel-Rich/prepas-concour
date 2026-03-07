package com.mutrix.prepa.infrastructure.implementations;

import com.mutrix.prepa.domaines.models.ConcoursSessions;
import com.mutrix.prepa.domaines.interfaces.ConcoursSessionServices;
import com.mutrix.prepa.infrastructure.mappers.ConcoursSessionEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursSessionRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursSessionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ConcoursSessionServicesImplement implements ConcoursSessionServices {

    private final ConcoursSessionRepository concoursSessionRepository;
    private final ConcoursRepository concoursRepository;

    @Override
    public ConcoursSessions createConcoursSession(ConcoursSessions concoursSession) {
        final ConcoursEntity concoursEntity = concoursRepository.findById(concoursSession.getConcoursId())
                .orElseThrow(
                        () -> new RuntimeException("Concours not found with id: " + concoursSession.getConcoursId()));
        final ConcoursSessionEntity concoursSessionEntity = ConcoursSessionEntityMapper.domainToEntity(concoursSession,
                concoursEntity);
        final ConcoursSessionEntity savedEntity = concoursSessionRepository.save(concoursSessionEntity);
        return ConcoursSessionEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ConcoursSessions> getConcoursSessionById(UUID id) {
        final Optional<ConcoursSessionEntity> concoursSessionEntityOptional = concoursSessionRepository.findById(id);
        return concoursSessionEntityOptional.map(ConcoursSessionEntityMapper::toDomain);
    }

    @Override
    public ConcoursSessions updateConcoursSession(ConcoursSessions concoursSession) {
        final ConcoursEntity concoursEntity = concoursRepository.findById(concoursSession.getConcoursId())
                .orElseThrow(
                        () -> new RuntimeException("Concours not found with id: " + concoursSession.getConcoursId()));
        final ConcoursSessionEntity concoursSessionEntity = ConcoursSessionEntityMapper.domainToEntity(concoursSession,
                concoursEntity);
        final ConcoursSessionEntity savedEntity = concoursSessionRepository.save(concoursSessionEntity);
        return ConcoursSessionEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Page<ConcoursSessions> getAllConcoursSessions(Integer page, Integer size) {
        final Pageable pageable = PageRequest.of(page, size);
        final Page<ConcoursSessionEntity> concoursSessionEntityPage = concoursSessionRepository.findAll(pageable);
        return concoursSessionEntityPage.map(ConcoursSessionEntityMapper::toDomain);
    }

    @Override
    public Page<ConcoursSessions> getAllByConcoursId(UUID concoursId, Integer page, Integer size) {
        final Pageable pageable = PageRequest.of(page, size);
        final Page<ConcoursSessionEntity> concoursSessionEntityPage = concoursSessionRepository
                .findByConcours_Id(concoursId, pageable);
        return concoursSessionEntityPage.map(ConcoursSessionEntityMapper::toDomain);
    }

    @Override
    public void deleteConcoursSession(UUID id) {
        concoursSessionRepository.deleteById(id);
    }
}
