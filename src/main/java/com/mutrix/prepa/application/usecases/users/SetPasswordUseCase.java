package com.mutrix.prepa.application.usecases.users;

import com.mutrix.prepa.application.dto.commandes.users.SetPasswordDto;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.models.UserModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@UseCase
public class SetPasswordUseCase {

    private final UsersServices usersServices;
    private final PasswordEncoder passwordEncoder;

    public void execute(UserModel user, SetPasswordDto dto) {
        if (user.getPasswordHash() != null) {
            throw new IllegalStateException("Un mot de passe est déjà défini pour ce compte");
        }
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword().toString()));
        usersServices.updateUser(user, null);
    }
}
