package com.mutrix.prepa.infrastructure.implementations;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.mutrix.prepa.domaines.models.OtpSession;
import com.mutrix.prepa.domaines.interfaces.OtpSessionService;
import com.mutrix.prepa.infrastructure.mappers.OtpSessionsMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.OtpSessionRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.OtpSessionEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OtpSessionServiceImplement implements OtpSessionService {
    private final OtpSessionRepository otpSessionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OtpSession createOtpSession(OtpSession otpSession, String clearOtp) {
        String otpHash = passwordEncoder.encode(clearOtp);
        otpSession.setOtpHash(otpHash);
        otpSession.setExpiresAt(System.currentTimeMillis() + 60 * 1000); // OTP expires in 5 minutes
        final OtpSessionEntity entity = OtpSessionsMapper.toEntity(otpSession);
        final OtpSessionEntity savedEntity = otpSessionRepository.save(entity);
        return OtpSessionsMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<OtpSession> getOtpSessionByEmail(String email) {
        return otpSessionRepository.findByEmail(email)
                .map(OtpSessionsMapper::toDomain);
    }

    @Override
    public Optional<OtpSession> getOtpSessionByPhone(String phone) {
        return otpSessionRepository.findByPhone(phone)
                .map(OtpSessionsMapper::toDomain);
    }

    @Override
    public Optional<OtpSession> getOtpSessionById(UUID id) {
        return otpSessionRepository.findById(id)
                .map(OtpSessionsMapper::toDomain);
    }

    @Override
    public void deleteOtpSessionById(UUID id) {
        otpSessionRepository.deleteById(id);
    }

    @Override
    public OtpSession validateOtp(UUID id, String otp) {
        final Optional<OtpSessionEntity> optionalEntity = otpSessionRepository.findById(id);
        if (optionalEntity.isEmpty()) {
            throw new RuntimeException("Invalid OTP session code");
        }
        if (optionalEntity.get().getExpiresAt() < System.currentTimeMillis()) {
            throw new RuntimeException("OTP code has expired");
        }
        final OtpSessionEntity entity = optionalEntity.get();
        if (!passwordEncoder.matches(otp, entity.getOtpHash())) {
            throw new RuntimeException("Invalid OTP code");
        }
        return OtpSessionsMapper.toDomain(entity);

    }
}
