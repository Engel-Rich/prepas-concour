package com.mutrix.prepa.infrastructure.implementations;

import com.mutrix.prepa.domaines.models.Concours;
import com.mutrix.prepa.domaines.interfaces.ConcoursServices;
import com.mutrix.prepa.infrastructure.mappers.ConcoursEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.ConcoursRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.ConcoursEntity;
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
public class ConcoursServicesImplement implements ConcoursServices {

    private final ConcoursRepository concoursRepository;

    @Override
    public Concours createConcours(Concours concours) {
        final ConcoursEntity concoursEntity = concoursRepository.save(ConcoursEntityMapper.toConcoursEntity(concours));
        return ConcoursEntityMapper.toConcoursDomainModel(concoursEntity);
    }

    @Override
    public Optional<Concours> getConcoursById(UUID id) {
        final Optional<ConcoursEntity> concoursEntity = concoursRepository.findById(id);
        return concoursEntity.map(ConcoursEntityMapper::toConcoursDomainModel);
    }

    @Override
    public Concours updateConcours(Concours concours) {
        final ConcoursEntity concoursEntity = concoursRepository.save(ConcoursEntityMapper.toConcoursEntity(concours));
        return ConcoursEntityMapper.toConcoursDomainModel(concoursEntity);
    }

    @Override
    public Page<Concours> getAllConcours(Integer page, Integer size) {
        final Pageable pageable = PageRequest.of(page, size);
        final Page<ConcoursEntity> concoursEntityPage = concoursRepository.findAll(pageable);
        return concoursEntityPage.map(ConcoursEntityMapper::toConcoursDomainModel);
    }

    @Override
    public void deleteConcours(String id) {
        concoursRepository.deleteById(UUID.fromString(id));
    }
}
