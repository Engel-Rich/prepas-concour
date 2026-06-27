package com.mutrix.prepa.infrastructure.implementations;

import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.domaines.interfaces.RolesServices;
import com.mutrix.prepa.infrastructure.mappers.RolesEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.RolesRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.RoleEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public Roles getRoleByName(String name) {
        return RolesEntityMapper.maFromEntity(
                repository.getByName(name).orElseThrow(() -> new RuntimeException("Role not found with this name")));
    }

    @Override
    public Optional<Roles> findRoleByName(String name) {
        return repository.getByName(name).map(RolesEntityMapper::maFromEntity);
    }

    // @PostConstruct
    // @Transactional
    // public void saveDefaultRules(){
    // List<String> rolesList = List.of("USER", "ADMIN","TEACHER",
    // "PARTNER","COMMERCIAL");
    //
    // rolesList.forEach(e->{
    // if(repository.getByName(e).isEmpty()){
    // saveRoles(new Roles(e));
    // }
    // });
    // }
}
