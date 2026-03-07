package com.mutrix.prepa.infrastructure.implementations;

import com.mutrix.prepa.domaines.models.Cours;
import com.mutrix.prepa.domaines.interfaces.CoursServices;
import com.mutrix.prepa.infrastructure.mappers.CoursEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.CoursRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.MatiereRepository;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.UserRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.MatiereEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CoursServicesImplement implements CoursServices {
    private final CoursRepository coursRepository;
    private final MatiereRepository matiereRepository;
    private final UserRepository userRepository;

    @Override
    public Cours save(Cours cours) {
        final MatiereEntity matiereEntity = matiereRepository.findById(cours.getMatiereId())
                .orElseThrow(() -> new RuntimeException("Matiere not found"));
        final Optional<UserEntity> userEntity = userRepository.findById(cours.getUserId());
        final CoursEntity entity = CoursEntityMapper.toCoursEntity(cours, matiereEntity, userEntity.orElse(null));
        final CoursEntity savedEntity = coursRepository.save(entity);
        return CoursEntityMapper.toDCoursDomain(savedEntity);
    }

    @Override
    public Optional<Cours> getCoursById(UUID id) {
        final Optional<CoursEntity> coursEntityOptional = coursRepository.findById(id);
        return coursEntityOptional.map(CoursEntityMapper::toDCoursDomain);
    }

    @Override
    public Cours updateCours(Cours cours) {
        final MatiereEntity matiereEntity = matiereRepository.findById(cours.getMatiereId())
                .orElseThrow(() -> new RuntimeException("Matiere not found"));
        final Optional<UserEntity> userEntity = userRepository.findById(cours.getUserId());
        final CoursEntity entity = CoursEntityMapper.toCoursEntity(cours, matiereEntity, userEntity.orElse(null));
        final CoursEntity savedEntity = coursRepository.save(entity);
        return CoursEntityMapper.toDCoursDomain(savedEntity);
    }

    @Override
    public Page<Cours> getAllCours(Integer page, Integer size) {
        final Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        final Page<CoursEntity> coursEntityPage = coursRepository.findAll(pageable);
        return coursEntityPage.map(CoursEntityMapper::toDCoursDomain);
    }

    @Override
    public Page<Cours> getAllByMatiereId(UUID matiereId, Pageable pageable) {
        final Page<CoursEntity> coursEntityPage = coursRepository.findByMatiere_Id(matiereId, pageable);
        return coursEntityPage.map(CoursEntityMapper::toDCoursDomain);
    }

    @Override
    public void deleteCours(UUID id) {
        coursRepository.deleteById(id);
    }
}
