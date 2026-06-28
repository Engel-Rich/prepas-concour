package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.application.dto.commandes.auth.ResetPasswordValidateDto;
import com.mutrix.prepa.application.dto.commandes.users.CreateFirebaseUserDto;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.interfaces.OtpSessionService;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.models.OtpSession;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.services.FirebaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@UseCase
public class ResetPasswordValidateUseCase {

    private final OtpSessionService otpSessionService;
    private final UsersServices usersServices;
    private final FirebaseService firebaseService;
    private final PasswordEncoder passwordEncoder;

    public void execute(ResetPasswordValidateDto dto) {
        // 1. Valider l'OTP
        OtpSession session = otpSessionService.validateOtp(dto.getOtpSessionId(), dto.getOtp());

        // 2. Retrouver l'utilisateur via l'email ou le téléphone stocké dans la session
        UserModel userModel;
        if (session.getEmail() != null && !session.getEmail().isBlank()) {
            userModel = usersServices.getUserByEmail(session.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        } else if (session.getPhone() != null && !session.getPhone().isBlank()) {
            userModel = usersServices.getUserByPhone(session.getPhone())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        } else {
            throw new RuntimeException("Session OTP invalide : aucun contact associé");
        }

        // 3. Hasher et mettre à jour le mot de passe en base
        if(passwordEncoder.matches(dto.getNewPassword(), userModel.getPasswordHash())){
            throw new RuntimeException("Ce mot de passe est déjà utilisé, bien vouloir choisir un nouveau mot de passe");
        }
        String newHash = passwordEncoder.encode(dto.getNewPassword());
        userModel.setPasswordHash(newHash);
        usersServices.updateUser(userModel);

        // 4. Mettre à jour le mot de passe sur Firebase (Admin SDK)
        if (userModel.getFirebaseUid() != null) {
            try {
                firebaseService.updateUser(userModel.getFirebaseUid(),
                        CreateFirebaseUserDto.builder()
                                .password(dto.getNewPassword())
                                .build());
            } catch (Exception e) {
                // On ne bloque pas si Firebase échoue — la mise à jour locale est prioritaire
                // Le prochain login via Firebase SDK utilisera le mot de passe local
                throw new RuntimeException("Mot de passe mis à jour localement, mais échec Firebase : " + e.getMessage());
            }
        }

        // 5. Invalider la session OTP
        otpSessionService.deleteOtpSessionById(session.getId());
    }
}
