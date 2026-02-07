package com.mutrix.prepa.infrastructure.persistence.repositories_implement;

import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.domaines.repository_interfaces.MatiereRepositoryInterface;
import com.mutrix.prepa.infrastructure.mappers.MatieresEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.MatiereRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.MatiereEntity;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
public class MatiereRepositoryImplement implements MatiereRepositoryInterface {

    final private MatiereRepository matiereRepository;

    @Override
    public Matieres createMatiere(Matieres matiere) {
        final MatiereEntity matiereEntity = MatieresEntityMapper.toEntity(matiere);
        final MatiereEntity savedMatiereEntity = matiereRepository.save(matiereEntity);
        return MatieresEntityMapper.toDomain(savedMatiereEntity);
    }

    @Override
    public Optional<Matieres> getMatiereById(UUID id) {
        final Optional<MatiereEntity> matiereEntityOptional = matiereRepository.findById(id);
        return matiereEntityOptional.map(MatieresEntityMapper::toDomain);
    }

    @Override
    public Matieres updateMatiere(Matieres matiere) {
        final MatiereEntity matiereEntity = MatieresEntityMapper.toEntity(matiere);
        final MatiereEntity updatedMatiereEntity = matiereRepository.save(matiereEntity);
        return MatieresEntityMapper.toDomain(updatedMatiereEntity);
    }

    @Override
    public Page<Matieres> getAllMatieres(Integer page, Integer size) {
        final Pageable pageable = PageRequest.of(page, size);
        final Page<MatiereEntity> matiereEntityPage = matiereRepository.findAll(pageable);
        return matiereEntityPage.map(MatieresEntityMapper::toDomain);
    }

    @Override
    public void deleteMatiere(UUID id) {
        matiereRepository.deleteById(id);
    }
}
