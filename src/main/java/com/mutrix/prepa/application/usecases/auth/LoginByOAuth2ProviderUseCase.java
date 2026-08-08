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

            // 1. Données Firebase (UID vérifié en amont par FirebaseAuthFilter)
            FirebaseUser firebaseUser = firebaseService.getUserByUid(dto.getFirebaseUid());

            // 2. Chercher d'abord par firebaseUid
            var userOpt = usersServices.getUserByFirebaseUid(dto.getFirebaseUid());

            // 3. Si pas trouvé par UID → chercher par email pour merger un compte existant
            //    (ex : inscrit par OTP puis connexion Google avec le même email)
            if (userOpt.isEmpty() && firebaseUser.getEmail() != null) {
                userOpt = usersServices.getUserByEmail(firebaseUser.getEmail());
                if (userOpt.isPresent()) {
                    // Migrer le firebaseUid vers celui du provider OAuth2
                    userOpt.get().setFirebaseUid(firebaseUser.getUid());
                }
            }

            if (userOpt.isEmpty()) {
                // Nouveau compte OAuth2
                UserModel newUser = buildFromFirebase(firebaseUser, userRole);
                UserModel saved = usersServices.createUser(newUser);
                return UserResponseMapper.mapFromUser(saved);
            }

            // 4. Compte existant → merger les données Firebase
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

        // L'email est vérifié par le provider OAuth2 (Google/Apple)
        if (Boolean.TRUE.equals(fb.getEmailVerified()))
            user.setHasEmailVerified(true);

        if ((user.getName() == null || user.getName().isBlank()) && fb.getDisplayName() != null)
            user.setName(fb.getDisplayName());

        // Ne remplace le téléphone que s'il était absent — le user peut avoir son propre numéro
        if ((user.getPhone() == null || user.getPhone().isBlank()) && fb.getPhoneNumber() != null)
            user.setPhone(fb.getPhoneNumber());

        if (fb.getPhotoUrl() != null && (user.getProfilePictureUrl() == null || user.getProfilePictureUrl().isBlank()))
            user.setProfilePictureUrl(fb.getPhotoUrl());

        if (fb.getMetadata() != null)
            user.setMetadata(fb.getMetadata());

        return user;
    }
}
