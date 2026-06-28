package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.application.dto.commandes.auth.ResetPasswordInitiateDto;
import com.mutrix.prepa.application.dto.response.OtpResponse;
import com.mutrix.prepa.cors.NumberGenerator;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.interfaces.OtpSessionService;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.models.OtpSession;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import com.mutrix.prepa.domaines.valueobjects.OtpType;
import com.mutrix.prepa.infrastructure.services.NotificationServiceImplement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@UseCase
public class ResetPasswordInitiateUseCase {

    private final UsersServices usersServices;
    private final OtpSessionService otpSessionService;
    private final NotificationServiceImplement notificationService;

    public OtpResponse execute(ResetPasswordInitiateDto dto) {
        // Vérifier que l'utilisateur existe
        UserModel userModel;
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            userModel = usersServices.getUserByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("Aucun compte trouvé avec cet email"));
        } else if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            userModel = usersServices.getUserByPhone(dto.getPhoneNumber())
                    .orElseThrow(() -> new RuntimeException("Aucun compte trouvé avec ce numéro de téléphone"));
        } else {
            throw new RuntimeException("Email ou numéro de téléphone requis");
        }

        String otp = NumberGenerator.generateRandomSixDigitInt();
        log.info("Nouvel OTP {}",otp);
        OtpSession session = OtpSession.builder()
                .email(dto.getEmail())
                .phone(dto.getPhoneNumber())
                .otpType(OtpType.PASSWORD_RESET)
                .build();
        OtpSession savedSession = otpSessionService.createOtpSession(session, otp);

        NotificationType channel = InitiateLoginUseCase.resolveChannel(
                dto.getEmail(), dto.getPhoneNumber(), dto.getNotificationType());
        sendOtp(channel, dto.getEmail(), dto.getPhoneNumber(), userModel.getName(), otp);

        return OtpResponse.builder()
                .otpId(savedSession.getId())
                .expirationTime(savedSession.getExpiresAt())
                .phoneNumber(savedSession.getPhone())
                .email(savedSession.getEmail())
                .build();
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
