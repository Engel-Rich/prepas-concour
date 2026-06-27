package com.mutrix.prepa.infrastructure.seeders;

import com.mutrix.prepa.application.dto.commandes.users.CreateFirebaseUserDto;
import com.mutrix.prepa.domaines.models.FirebaseUser;
import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.interfaces.RolesServices;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.services.FirebaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class AdminSeeder implements ApplicationRunner {

    private static final String ADMIN_EMAIL    = "admin@mutrix.org";
    private static final String ADMIN_PASSWORD = "@dmin123.";
    private static final String ADMIN_NAME     = "Administrateur Mutrix";
    private static final String ADMIN_ROLE     = "ADMIN";

    private final UsersServices   usersServices;
    private final RolesServices   rolesServices;
    private final FirebaseService firebaseService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        // ── 1. S'assurer que le rôle ADMIN existe ────────────────────────────
        Roles adminRole = rolesServices.findRoleByName(ADMIN_ROLE)
                .orElseGet(() -> {
                    log.warn("Rôle '{}' absent en base, création...", ADMIN_ROLE);
                    return rolesServices.saveRoles(new Roles(ADMIN_ROLE));
                });

        // ── 2. Chercher l'utilisateur admin ──────────────────────────────────
        Optional<UserModel> existingOpt = usersServices.getUserByEmail(ADMIN_EMAIL);

        if (existingOpt.isPresent()) {
            UserModel existing = existingOpt.get();

            boolean hasAdminRole = existing.getRoles() != null &&
                    existing.getRoles().stream().anyMatch(r -> ADMIN_ROLE.equals(r.getName()));

            if (hasAdminRole) {
                log.info("Admin '{}' existe déjà avec le rôle {}, seeder ignoré.", ADMIN_EMAIL, ADMIN_ROLE);
                return;
            }

            // ── 2b. User existe mais sans le rôle ADMIN → on l'ajoute ────────
            log.warn("Admin '{}' existe mais n'a pas le rôle {} — assignation...", ADMIN_EMAIL, ADMIN_ROLE);

            List<Roles> updatedRoles = new ArrayList<>();
            if (existing.getRoles() != null) {
                updatedRoles.addAll(existing.getRoles());
            }
            updatedRoles.add(adminRole);
            existing.setRoles(updatedRoles);

            usersServices.updateUser(existing);
            log.info("Rôle {} assigné à '{}' avec succès.", ADMIN_ROLE, ADMIN_EMAIL);
            return;
        }

        // ── 3. Créer l'utilisateur admin (premier démarrage) ─────────────────
        log.info("Création de l'administrateur '{}'...", ADMIN_EMAIL);

        FirebaseUser firebaseUser = null;
        try {
            firebaseUser = firebaseService.getUserByEmail(ADMIN_EMAIL);
        } catch (Exception ignored) {
            // user Firebase inexistant → on le créera ci-dessous
        }

        if (firebaseUser == null) {
            firebaseUser = firebaseService.createUser(CreateFirebaseUserDto.builder()
                    .email(ADMIN_EMAIL)
                    .password(ADMIN_PASSWORD)
                    .displayName(ADMIN_NAME)
                    .emailVerified(true)
                    .disabled(false)
                    .build());
        }

        if (firebaseUser == null) {
            log.error("Impossible de créer l'utilisateur Firebase pour l'admin — seeder abandonné.");
            return;
        }

        String passwordHash = passwordEncoder.encode(ADMIN_PASSWORD);
        UserModel admin = UserModel.builder()
                .firebaseUid(firebaseUser.getUid())
                .name(ADMIN_NAME)
                .email(ADMIN_EMAIL)
                .passwordHash(passwordHash)
                .roles(List.of(adminRole))
                .isActive(true)
                .hasEmailVerified(true)
                .hasPhoneVerified(false)
                .build();
        usersServices.createUser(admin);
        log.info("Administrateur '{}' créé avec succès avec le rôle {}.", ADMIN_EMAIL, ADMIN_ROLE);
    }
}
