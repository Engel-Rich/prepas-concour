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
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import com.mutrix.prepa.domaines.valueobjects.OtpType;
import com.mutrix.prepa.infrastructure.services.NotificationServiceImplement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@RequiredArgsConstructor
@UseCase
public class InitiateLoginUseCase {
    private static final Logger log = LoggerFactory.getLogger(InitiateLoginUseCase.class);
    private final UsersServices usersServices;
    private final OtpSessionService otpSessionService;
    private final NotificationServiceImplement notificationService;
    private final PasswordEncoder passwordEncoder;

    public OtpResponse execute(LoginCommand dto) {
        UserModel userModel;
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            final Optional<UserModel> userByEmail = usersServices.getUserByEmail(dto.getEmail());
            userModel = userByEmail.orElseThrow(() -> new RuntimeException("Invalid email or password"));
        } else if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
            final Optional<UserModel> userByPhone = usersServices.getUserByPhone(dto.getPhoneNumber());
            userModel = userByPhone.orElseThrow(() -> new RuntimeException("Invalid phone number credentials"));
        } else {
            throw new RuntimeException("Either email or phone must be provided");
        }

        boolean checkPassword = passwordEncoder.matches(dto.getPassword().toString(), userModel.getPasswordHash());
        if (!checkPassword) {
            throw new RuntimeException(
                    "Invalid " + (dto.getPhoneNumber() != null ? "phone number" : "email") + " or password");
        }

        final String otp = NumberGenerator.generateRandomSixDigitInt();
        final OtpSession session = OtpSession.builder()
                .email(dto.getEmail())
                .phone(dto.getPhoneNumber())
                .otpType(OtpType.LOGIN)
                .build();

        NotificationType channel = resolveChannel(dto.getEmail(), dto.getPhoneNumber(), dto.getNotificationType());
        sendOtp(channel, dto.getEmail(), dto.getPhoneNumber(), userModel.getName(), otp);

        OtpSession savedSession = otpSessionService.createOtpSession(session, otp);
        return OtpResponse.builder()
                .otpId(savedSession.getId())
                .expirationTime(savedSession.getExpiresAt())
                .phoneNumber(savedSession.getPhone())
                .email(savedSession.getEmail())
                .build();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    static NotificationType resolveChannel(String email, String phone, NotificationType requested) {
        if (requested != null) return requested;
        if (email != null && !email.isBlank()) return NotificationType.EMAIL;
        if (phone != null && !phone.isBlank()) return NotificationType.SMS;
        throw new RuntimeException("Impossible de déterminer le canal de notification");
    }

    private void sendOtp(NotificationType channel, String email, String phone, String fullName, String otp) {
        log.info("Sending OTP {} for {} to {}", otp, fullName, email);
        switch (channel) {
            case EMAIL   -> notificationService.sendOtpEmail(email, otp, fullName);
            case SMS     -> notificationService.sendOtpSms(phone, otp);
            case WHATSAPP-> notificationService.sendWhatsAppOtp(phone, otp);
            default      -> throw new RuntimeException("Canal de notification non supporté : " + channel);
        }
    }
}
