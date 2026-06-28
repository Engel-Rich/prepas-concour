package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.application.dto.commandes.auth.RegisterCommand;
import com.mutrix.prepa.application.dto.response.OtpResponse;
import com.mutrix.prepa.cors.NumberGenerator;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.OtpSession;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.interfaces.OtpSessionService;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import com.mutrix.prepa.domaines.valueobjects.OtpType;
import com.mutrix.prepa.infrastructure.services.NotificationServiceImplement;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
@UseCase
public class RegistrationsInitiateUseCase {

    private final UsersServices usersServices;
    public final OtpSessionService otpSessionService;
    private final NotificationServiceImplement notificationService;

    public OtpResponse execute(RegisterCommand dto) {
        try {
            final Optional<UserModel> usersEmail = usersServices.getUserByEmail(dto.getEmail());
            if (usersEmail.isPresent()) {
                throw new RuntimeException("User with this email already exists");
            }
            final Optional<UserModel> usersPhone = usersServices.getUserByPhone(dto.getPhoneNumber());
            if (usersPhone.isPresent()) {
                throw new RuntimeException("User with this phone number already exists");
            }

            String otp = NumberGenerator.generateRandomSixDigitInt();

            final OtpSession otpSession = OtpSession.builder()
                    .email(dto.getEmail())
                    .phone(dto.getPhoneNumber())
                    .fullName(dto.getFullName())
                    .otpType(OtpType.REGISTRATION)
                    .build();
            final OtpSession savedSession = otpSessionService.createOtpSession(otpSession, otp);

            NotificationType channel = InitiateLoginUseCase.resolveChannel(
                    dto.getEmail(), dto.getPhoneNumber(), dto.getNotificationType());
            sendOtp(channel, dto.getEmail(), dto.getPhoneNumber(), dto.getFullName(), otp);

            return OtpResponse.builder()
                    .otpId(savedSession.getId())
                    .phoneNumber(savedSession.getPhone())
                    .email(savedSession.getEmail())
                    .expirationTime(savedSession.getExpiresAt())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate registration: " + e.getMessage());
        }
    }

    private void sendOtp(NotificationType channel, String email, String phone, String fullName, String otp) {
        switch (channel) {
            case EMAIL    -> notificationService.sendOtpEmail(email, otp, fullName);
            case SMS      -> notificationService.sendOtpSms(phone, otp);
            case WHATSAPP -> notificationService.sendWhatsAppOtp(phone, otp);
            default       -> throw new RuntimeException("Canal de notification non supporté : " + channel);
        }
    }
}
