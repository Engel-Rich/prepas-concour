package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.application.dto.commandes.auth.UpdatePasswordDto;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.models.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@UseCase
public class UpdatePasswordUseCase {

    private final UsersServices usersServices;
    private final PasswordEncoder passwordEncoder;

    public void execute(UserModel userModel, UpdatePasswordDto dto) {
        boolean valid = passwordEncoder.matches(dto.getOldPassword().toString(), userModel.getPasswordHash());
        if (!valid) {
            throw new RuntimeException("L'ancien mot de passe est incorrect");
        }

        String newHash = passwordEncoder.encode(dto.getNewPassword().toString());
        userModel.setPasswordHash(newHash);
        usersServices.updateUser(userModel, dto.getNewPassword().toString());
    }
}
