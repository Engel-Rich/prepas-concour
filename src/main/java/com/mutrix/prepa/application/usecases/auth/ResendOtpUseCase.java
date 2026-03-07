package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.application.dto.commandes.auth.ResendOtpCommand;
import com.mutrix.prepa.application.dto.response.OtpResponse;
import com.mutrix.prepa.cors.NumberGenerator;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.OtpSession;
import com.mutrix.prepa.domaines.interfaces.OtpSessionService;
import com.mutrix.prepa.domaines.services.NotificationService;
import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class ResendOtpUseCase {
    private final OtpSessionService otpSessionRepository;
    private final NotificationService notificationService;

    public OtpResponse execute(ResendOtpCommand dto) {
        try {
            OtpSession session = otpSessionRepository.getOtpSessionById(dto.getOtpSessionId())
                    .orElseThrow(() -> new RuntimeException("OTP session not found for ID: " + dto.getOtpSessionId()));
            if (session.getExpiresAt() > System.currentTimeMillis()) {
                throw new RuntimeException("The Otp session is still valid, please wait until it expires before requesting a new OTP.");
            }
            String newOtp = NumberGenerator.generateRandomSixDigitInt();
            session.setId(null);
            otpSessionRepository.deleteOtpSessionById(dto.getOtpSessionId());
            if ((dto.getType().equals(NotificationType.SMS) || dto.getType().equals(NotificationType.WHATSAPP)) && session.getPhone() != null) {
                if (dto.getType().equals(NotificationType.SMS)) {
                    notificationService.sendOtpSms(session.getPhone(), newOtp);
                } else {
                    notificationService.sendWhatsAppOtp(session.getPhone(), newOtp);
                }
            } else if (dto.getType().equals(NotificationType.EMAIL) && session.getEmail() != null) {
                notificationService.sendOtpEmail(session.getEmail(), newOtp);
            } else {
                if (session.getPhone() != null) {
                    notificationService.sendOtpSms(session.getPhone(), newOtp);
                } else if (session.getEmail() != null) {
                    notificationService.sendOtpEmail(session.getEmail(), newOtp);
                }
            }
            OtpSession newSession = otpSessionRepository.createOtpSession(session, newOtp);
            return OtpResponse.builder()
                    .otpId(newSession.getId())
                    .phoneNumber(newSession.getPhone())
                    .email(newSession.getEmail())
                    .expirationTime(newSession.getExpiresAt())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to resend OTP: " + e.getMessage());
        }
    }
}
