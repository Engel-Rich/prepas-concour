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

    private static final String ADMIN_ROLE = "ADMIN";

    /** Liste des administrateurs à seeder au démarrage */
    private static final List<AdminEntry> ADMINS = List.of(
            new AdminEntry("ondouel@gmail.com",                    "@dmin123.",        "Administrateur Mutrix"),
            new AdminEntry("engelbertrichelieutsinda@gmail.com",  "@endev-agc.com",   "Engelbert Richelieu Tsinda")
    );

    private final UsersServices   usersServices;
    private final RolesServices   rolesServices;
    private final FirebaseService firebaseService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        // S'assurer que le rôle ADMIN existe
        Roles adminRole = rolesServices.findRoleByName(ADMIN_ROLE)
                .orElseGet(() -> {
                    log.warn("Rôle '{}' absent en base, création...", ADMIN_ROLE);
                    return rolesServices.saveRoles(new Roles(ADMIN_ROLE));
                });

        for (AdminEntry entry : ADMINS) {
            seedAdmin(entry, adminRole);
        }
    }

    private void seedAdmin(AdminEntry entry, Roles adminRole) {

        Optional<UserModel> existingOpt = usersServices.getUserByEmail(entry.email());

        if (existingOpt.isPresent()) {
            UserModel existing = existingOpt.get();
            boolean hasRole = existing.getRoles() != null &&
                    existing.getRoles().stream().anyMatch(r -> ADMIN_ROLE.equals(r.getName()));

            if (hasRole) {
                log.info("Admin '{}' existe déjà avec le rôle {}, seeder ignoré.", entry.email(), ADMIN_ROLE);
                return;
            }

            log.warn("Admin '{}' existe mais sans le rôle {} — assignation...", entry.email(), ADMIN_ROLE);
            List<Roles> roles = new ArrayList<>();
            if (existing.getRoles() != null) roles.addAll(existing.getRoles());
            roles.add(adminRole);
            existing.setRoles(roles);
            usersServices.updateUser(existing, null);
            log.info("Rôle {} assigné à '{}' avec succès.", ADMIN_ROLE, entry.email());
            return;
        }

        log.info("Création de l'administrateur '{}'...", entry.email());

        FirebaseUser firebaseUser = null;
        try {
            firebaseUser = firebaseService.getUserByEmail(entry.email());
        } catch (Exception ignored) {}

        if (firebaseUser == null) {
            firebaseUser = firebaseService.createUser(CreateFirebaseUserDto.builder()
                    .email(entry.email())
                    .password(entry.password())
                    .displayName(entry.displayName())
                    .emailVerified(true)
                    .disabled(false)
                    .build());
        }

        if (firebaseUser == null) {
            log.error("Impossible de créer l'utilisateur Firebase pour '{}' — ignoré.", entry.email());
            return;
        }

        UserModel admin = UserModel.builder()
                .firebaseUid(firebaseUser.getUid())
                .name(entry.displayName())
                .email(entry.email())
                .passwordHash(passwordEncoder.encode(entry.password()))
                .roles(List.of(adminRole))
                .isActive(true)
                .hasEmailVerified(true)
                .hasPhoneVerified(false)
                .build();

        usersServices.createUser(admin);
        log.info("Administrateur '{}' créé avec succès.", entry.email());
    }

    private record AdminEntry(String email, String password, String displayName) {}
}
