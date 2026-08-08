package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.application.dto.commandes.auth.LoginCompletionDto;
import com.mutrix.prepa.application.dto.mappers.UserResponseMapper;
import com.mutrix.prepa.application.dto.response.AuthResponse;
import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.OtpSession;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.interfaces.OtpSessionService;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.services.FirebaseService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class CompleteLoginUseCase {
    private final FirebaseService firebaseService;
    private final UsersServices usersServices;
    private final OtpSessionService otpSessionService;

    public AuthResponse execute(LoginCompletionDto dto) {
        try {
            UserModel userModel = null;
            OtpSession session = otpSessionService.validateOtp(dto.getOtpSessionId(),
                    String.format("%06d", dto.getOtp()));
            if (session.getEmail() != null) {
                userModel = usersServices.getUserByEmail(session.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                // L'OTP email vient d'être validé : marquer l'email comme vérifié
                if (!Boolean.TRUE.equals(userModel.getHasEmailVerified())) {
                    userModel.setHasEmailVerified(true);
                    usersServices.updateUser(userModel, null);
                }
            } else if (session.getPhone() != null) {
                userModel = usersServices.getUserByPhone(session.getPhone())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                // L'OTP SMS vient d'être validé : marquer le téléphone comme vérifié
                if (!Boolean.TRUE.equals(userModel.getHasPhoneVerified())) {
                    userModel.setHasPhoneVerified(true);
                    usersServices.updateUser(userModel, null);
                }
            } else {
                throw new RuntimeException("Invalid OTP session");
            }
            String token = firebaseService.createCustomToken(userModel.getFirebaseUid());
            UserResponse response = UserResponseMapper.mapFromUser(userModel);
            otpSessionService.deleteOtpSessionById(session.getId());
            return AuthResponse.builder().userResponse(response).token(token).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete login: " + e.getMessage());
        }
    }

}
