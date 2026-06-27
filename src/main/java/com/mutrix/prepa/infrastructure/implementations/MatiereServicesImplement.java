package com.mutrix.prepa.infrastructure.implementations;

import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.domaines.interfaces.MatiereServices;
import com.mutrix.prepa.infrastructure.mappers.MatieresEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.MatiereRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.MatiereEntity;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class MatiereServicesImplement implements MatiereServices {

    final private MatiereRepository matiereRepository;

    @Override
    public Matieres createMatiere(Matieres matiere) {
        final MatiereEntity matiereEntity = MatieresEntityMapper.toEntity(matiere);
        final MatiereEntity savedMatiereEntity = matiereRepository.save(matiereEntity);
        System.err.println(savedMatiereEntity.toString());
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
        // page est 1-indexé côté API ; PageRequest est 0-indexé
        final Pageable pageable = PageRequest.of(Math.max(0, page - 1), size);
        final Page<MatiereEntity> matiereEntityPage = matiereRepository.findAll(pageable);
        matiereEntityPage.forEach((matiereEntity) -> {
            System.err.println(matiereEntity.toString());
        });
        return matiereEntityPage.map(MatieresEntityMapper::toDomain);
    }

    @Override
    public void deleteMatiere(UUID id) {
        matiereRepository.deleteById(id);
    }
}
