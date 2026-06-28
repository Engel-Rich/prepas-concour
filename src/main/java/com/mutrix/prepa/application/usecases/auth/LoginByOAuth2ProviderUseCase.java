package com.mutrix.prepa.application.usecases.auth;

import java.util.List;

import com.mutrix.prepa.application.dto.commandes.auth.LoginOauth2Command;
import com.mutrix.prepa.application.dto.mappers.UserResponseMapper;
import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.FirebaseUser;
import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.interfaces.RolesServices;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.services.FirebaseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class LoginByOAuth2ProviderUseCase {
    private final FirebaseService firebaseService;
    private final UsersServices usersServices;
    private final RolesServices rolesServices;

    public UserResponse execute(LoginOauth2Command dto) {
        try {
            final Roles userRole = rolesServices.getRoleByName("USER");

            // 1. Récupérer les données Firebase via l'UID fourni dans le body.
            //    L'ID token est vérifié en amont par FirebaseAuthFilter (header Authorization).
            FirebaseUser firebaseUser = firebaseService.getUserByUid(dto.getFirebaseUid());

            // 2. Chercher l'utilisateur par son UID Firebase
            var userOpt = usersServices.getUserByFirebaseUid(dto.getFirebaseUid());

            if (userOpt.isEmpty()) {
                // Nouvel utilisateur OAuth2 — pas encore de compte dans le système
                UserModel newUser = buildFromFirebase(firebaseUser, userRole);
                UserModel saved = usersServices.createUser(newUser);
                return UserResponseMapper.mapFromUser(saved);
            }

            // 3. Utilisateur existant — mettre à jour ses données Firebase
            UserModel updated = usersServices.updateUser(mergeFromFirebase(userOpt.get(), firebaseUser), null);
            return UserResponseMapper.mapFromUser(updated);

        } catch (Exception e) {
            throw new RuntimeException("Failed to login with OAuth2 provider: " + e.getMessage());
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Construit un UserModel depuis les données Firebase (premier login OAuth2). */
    private static UserModel buildFromFirebase(FirebaseUser fb, Roles role) {
        return UserModel.builder()
                .email(fb.getEmail())
                .name(fb.getDisplayName())
                .phone(fb.getPhoneNumber())
                .firebaseUid(fb.getUid())
                .metadata(fb.getMetadata())
                .hasEmailVerified(Boolean.TRUE.equals(fb.getEmailVerified()))
                .hasPhoneVerified(fb.getPhoneNumber() != null && !fb.getPhoneNumber().isBlank())
                .roles(List.of(role))
                .build();
    }

    /**
     * Merge les données Firebase sur un utilisateur existant.
     * — L'email Firebase est mis à jour (source de vérité = provider OAuth2)
     * — Le nom n'est écrasé que s'il était vide en base
     * — Le phone Firebase est ajouté seulement s'il était absent en base
     * — La photo de profil est toujours mise à jour
     * — Le passwordHash n'est jamais touché
     */
    private static UserModel mergeFromFirebase(UserModel user, FirebaseUser fb) {
        if (fb.getEmail() != null)
            user.setEmail(fb.getEmail());

        if ((user.getName() == null || user.getName().isBlank()) && fb.getDisplayName() != null)
            user.setName(fb.getDisplayName());

        if ((user.getPhone() == null || user.getPhone().isBlank()) && fb.getPhoneNumber() != null)
            user.setPhone(fb.getPhoneNumber());

        if (fb.getPhotoUrl() != null)
            user.setProfilePictureUrl(fb.getPhotoUrl());

        if (fb.getMetadata() != null)
            user.setMetadata(fb.getMetadata());

        return user;
    }
}
