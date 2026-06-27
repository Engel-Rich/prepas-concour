package com.mutrix.prepa.infrastructure.seeders;

import com.mutrix.prepa.domaines.interfaces.RolesServices;
import com.mutrix.prepa.domaines.models.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeder qui s'assure que les rôles de base existent dans la base de données.
 * S'exécute en premier (Order = 1) pour que AdminSeeder puisse les retrouver.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class RolesSeeder implements ApplicationRunner {

    /** Rôles requis dans l'application. */
    private static final List<String> REQUIRED_ROLES = List.of(
            "ADMIN",
            "PARTNER",
            "PROFESSOR",
            "USER"
    );

    private final RolesServices rolesServices;

    @Override
    public void run(ApplicationArguments args) {
        log.info("RolesSeeder : vérification des rôles requis…");

        int created = 0;
        for (String roleName : REQUIRED_ROLES) {
            boolean exists = rolesServices.findRoleByName(roleName).isPresent();
            if (!exists) {
                rolesServices.saveRoles(new Roles(roleName));
                log.info("RolesSeeder : rôle '{}' créé.", roleName);
                created++;
            } else {
                log.debug("RolesSeeder : rôle '{}' déjà présent.", roleName);
            }
        }

        if (created == 0) {
            log.info("RolesSeeder : tous les rôles requis sont déjà en base, rien à faire.");
        } else {
            log.info("RolesSeeder : {} rôle(s) créé(s) avec succès.", created);
        }
    }
}
