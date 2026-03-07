package com.mutrix.prepa.domaines.interfaces;

import com.mutrix.prepa.domaines.models.OtpSession;

import java.util.Optional;
import java.util.UUID;

public interface OtpSessionService {

    public OtpSession createOtpSession(OtpSession otpSession, String clearOtp);

    public Optional<OtpSession> getOtpSessionByEmail(String email);

    public Optional<OtpSession> getOtpSessionByPhone(String phone);

    public Optional<OtpSession> getOtpSessionById(UUID id);

    public void deleteOtpSessionById(UUID id);

    public OtpSession validateOtp(UUID id, String otp);
}
