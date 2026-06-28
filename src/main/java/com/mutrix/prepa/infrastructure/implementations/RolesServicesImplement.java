package com.mutrix.prepa.infrastructure.implementations;

import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.domaines.interfaces.RolesServices;
import com.mutrix.prepa.infrastructure.mappers.RolesEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.RolesRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolesServicesImplement implements RolesServices {

    private final RolesRepository repository;

    @Override
    public Roles saveRoles(Roles roles) {
        RoleEntity entity = repository.save(RolesEntityMapper.mapToEntity(roles));
        return RolesEntityMapper.maFromEntity(entity);
    }

    @Override
    public List<Roles> findAll() {
        return repository.findAll()
                .stream()
                .map(RolesEntityMapper::maFromEntity)
                .toList();
    }

    @Override
    public Roles getRoleByName(String name) {
        return RolesEntityMapper.maFromEntity(
                repository.getByName(name)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + name)));
    }

    @Override
    public Optional<Roles> findRoleByName(String name) {
        return repository.getByName(name).map(RolesEntityMapper::maFromEntity);
    }

    @Override
    public Optional<Roles> findById(UUID id) {
        return repository.findById(id).map(RolesEntityMapper::maFromEntity);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
