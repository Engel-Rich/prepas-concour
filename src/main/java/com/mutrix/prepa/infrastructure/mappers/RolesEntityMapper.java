package com.mutrix.prepa.infrastructure.mappers;

import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.infrastructure.persistence.entities.RoleEntity;

public final class RolesEntityMapper {

    private RolesEntityMapper() {
    }

    public static Roles maFromEntity(RoleEntity entity) {
        return Roles.
                builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static RoleEntity mapToEntity(Roles roles) {
        return RoleEntity
                .builder()
                .id(roles.getId())
                .name(roles.getName())
                .build();
    }
}
