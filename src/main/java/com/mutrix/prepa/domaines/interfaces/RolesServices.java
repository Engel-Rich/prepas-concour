package com.mutrix.prepa.domaines.interfaces;

import com.mutrix.prepa.domaines.models.Roles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RolesServices {

    Roles saveRoles(Roles roles);

    List<Roles> findAll();

    /** Lève une RuntimeException si le rôle est introuvable. */
    Roles getRoleByName(String name);

    /** Retourne un Optional vide si le rôle n'existe pas (sans exception). */
    Optional<Roles> findRoleByName(String name);

    Optional<Roles> findById(UUID id);

    void deleteById(UUID id);

}
