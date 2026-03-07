package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.application.dto.commandes.auth.LoginCommand;
import com.mutrix.prepa.application.dto.response.OtpResponse;
import com.mutrix.prepa.cors.NumberGenerator;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.OtpSession;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.interfaces.OtpSessionService;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.services.NotificationService;
import com.mutrix.prepa.domaines.valueobjects.OtpType;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@UseCase
public class InitiateLoginUseCase {
    private final UsersServices usersServices;
    private final OtpSessionService otpSessionService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    public OtpResponse execute(LoginCommand dto) {
        UserModel userModel;
        if (dto.getEmail() != null) {
            final Optional<UserModel> userByEmail = usersServices.getUserByEmail(dto.getEmail());
            if (userByEmail.isEmpty()) {
                throw new RuntimeException("Invalid email or password");
                // throw new RuntimeException("User with email " + dto.getEmail() + " not
                // found");
            } else {
                userModel = userByEmail.get();
            }
        } else if (dto.getPhoneNumber() != null) {
            final Optional<UserModel> userByPhone = usersServices.getUserByPhone(dto.getPhoneNumber());
            if (userByPhone.isEmpty()) {
                throw new RuntimeException("Invalid phone number credentials");
                // throw new RuntimeException("User with phone " + dto.getPhoneNumber() + " not
                // found");
            } else {
                userModel = userByPhone.get();
            }
        } else {
            throw new RuntimeException("Either email or phone must be provided");
        }
        boolean checkPassword = passwordEncoder.matches(dto.getPassword().toString(), userModel.getPasswordHash());
        if (!checkPassword) {
            throw new RuntimeException(
                    "Invalid " + (dto.getPhoneNumber() != null ? "phone number" : "email ") + " or password");
        }
        final String otp = NumberGenerator.generateRandomSixDigitInt();
        final OtpSession session = OtpSession.builder()
                .email(dto.getEmail())
                .phone(dto.getPhoneNumber())
                .otpType(OtpType.LOGIN)
                .build();
        if (dto.getEmail() != null) {
            notificationService.sendOtpEmail(dto.getEmail(), otp);
        } else {
            notificationService.sendOtpSms(dto.getPhoneNumber(), otp);
        }
        OtpSession savedSession = otpSessionService.createOtpSession(session, otp);
        return OtpResponse.builder()
                .otpId(savedSession.getId())
                .expirationTime(savedSession.getExpiresAt())
                .phoneNumber(savedSession.getPhone())
                .email(savedSession.getEmail())
                .build();
    }
}
