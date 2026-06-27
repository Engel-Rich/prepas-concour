package com.mutrix.prepa.domaines.interfaces;

import com.mutrix.prepa.domaines.models.Roles;

import java.util.Optional;

public interface RolesServices {

    Roles saveRoles(Roles roles);

    /** Lève une RuntimeException si le rôle est introuvable. */
    Roles getRoleByName(String name);

    /** Retourne un Optional vide si le rôle n'existe pas (sans exception). */
    Optional<Roles> findRoleByName(String name);

}
