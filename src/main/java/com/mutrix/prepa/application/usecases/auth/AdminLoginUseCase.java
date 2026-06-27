package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.application.dto.commandes.auth.AdminLoginCommand;
import com.mutrix.prepa.application.dto.mappers.UserResponseMapper;
import com.mutrix.prepa.application.dto.response.AuthResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.services.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

@UseCase
@RequiredArgsConstructor
public class AdminLoginUseCase {

    private final UsersServices usersServices;
    private final FirebaseService firebaseService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse execute(AdminLoginCommand command) {
        UserModel user = usersServices.getUserByEmail(command.getEmail())
                .orElseThrow(() -> new RuntimeException("Identifiants incorrects"));

        // Vérifier que l'utilisateur a le rôle ADMIN
        boolean isAdmin = user.getRoles() != null && user.getRoles().stream()
                .map(Roles::getName)
                .anyMatch("ADMIN"::equals);
        if (!isAdmin) {
            throw new AccessDeniedException("Accès refusé : l'utilisateur n'est pas administrateur");
        }

        // Vérifier le mot de passe
        if (user.getPasswordHash() == null || !passwordEncoder.matches(command.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Identifiants incorrects");
        }

        String token = firebaseService.createCustomToken(user.getFirebaseUid());
        return AuthResponse.builder()
                .userResponse(UserResponseMapper.mapFromUser(user))
                .token(token)
                .build();
    }
}
