package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.application.dto.commandes.auth.UpdatePasswordDto;
import com.mutrix.prepa.application.dto.commandes.users.CreateFirebaseUserDto;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.services.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@UseCase
public class UpdatePasswordUseCase {

    private final UsersServices usersServices;
    private final FirebaseService firebaseService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Met à jour le mot de passe de l'utilisateur authentifié.
     *
     * @param userModel  Utilisateur courant (injecté depuis le SecurityContext)
     * @param dto        Ancien + nouveau mot de passe (code PIN numérique)
     */
    public void execute(UserModel userModel, UpdatePasswordDto dto) {
        // 1. Vérifier l'ancien mot de passe
        boolean valid = passwordEncoder.matches(dto.getOldPassword().toString(), userModel.getPasswordHash());
        if (!valid) {
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }

        // 2. Hasher et persister le nouveau mot de passe
        String newHash = passwordEncoder.encode(dto.getNewPassword().toString());
        userModel.setPasswordHash(newHash);
        usersServices.updateUser(userModel);

        // 3. Mettre à jour sur Firebase
        if (userModel.getFirebaseUid() != null) {
            try {
                firebaseService.updateUser(userModel.getFirebaseUid(),
                        CreateFirebaseUserDto.builder()
                                .password(dto.getNewPassword().toString())
                                .build());
            } catch (Exception e) {
                throw new RuntimeException("Mot de passe mis à jour localement, mais échec Firebase : " + e.getMessage());
            }
        }
    }
}
