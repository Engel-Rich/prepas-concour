package com.mutrix.prepa.application.usecases.auth;

import com.mutrix.prepa.domaines.valueobjects.NotificationType;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.mutrix.prepa.application.dto.commandes.auth.CompleteRegistrationDto;
import com.mutrix.prepa.application.dto.commandes.users.CreateFirebaseUserDto;
import com.mutrix.prepa.application.dto.mappers.UserResponseMapper;
import com.mutrix.prepa.application.dto.response.AuthResponse;
import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.FirebaseUser;
import com.mutrix.prepa.domaines.models.OtpSession;
import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.interfaces.OtpSessionService;
import com.mutrix.prepa.domaines.interfaces.RolesServices;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.services.FirebaseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class CompleteRegistrationUseCase {

    private final FirebaseService firebaseService;
    private final PasswordEncoder passwordEncoder;
    private final UsersServices usersServices;
    private final OtpSessionService otpSessionService;
    private final RolesServices rolesServices;

    public AuthResponse execute(CompleteRegistrationDto dto) {
        try {
            final Roles roles = rolesServices.getRoleByName("USER");
            OtpSession validatedSession = otpSessionService.validateOtp(dto.getOtpSessionId(),
                    String.format("%06d", dto.getOtp()));
            // Récupère le canal utilisé pour l'OTP depuis les métadonnées de session
            String channelStr = validatedSession.getMetadata() != null
                    ? (String) validatedSession.getMetadata().getOrDefault("channel", "EMAIL")
                    : "EMAIL";
            NotificationType channel = NotificationType.valueOf(channelStr);
            boolean verifiedByEmail = channel == NotificationType.EMAIL;
            boolean verifiedByPhone = channel == NotificationType.SMS || channel == NotificationType.WHATSAPP;

            String rawPhone = validatedSession.getPhone();
            String phone = null;
            if (rawPhone != null) {
                // Le mobile envoie déjà le numéro en format international (+CC + numéro local)
                String digits = rawPhone.replaceAll("[^0-9]", "");
                if (digits.startsWith("00")) digits = digits.substring(2);
                phone = "+" + digits;
            }

            CreateFirebaseUserDto createFirebaseUserDto = CreateFirebaseUserDto.builder()
                    .email(validatedSession.getEmail())
                    .password(dto.getPassword().toString())
                    .phoneNumber(phone)
                    .displayName(validatedSession.getFullName())
                    .build();
            FirebaseUser firebaseUser = firebaseService.createUser(createFirebaseUserDto);

            UserModel userModel = UserModel.builder()
                    .email(validatedSession.getEmail())
                    .phone(validatedSession.getPhone())
                    .name(validatedSession.getFullName())
                    .firebaseUid(firebaseUser.getUid())
                    .passwordHash(passwordEncoder.encode(dto.getPassword().toString()))
                    .metadata(firebaseUser.getMetadata())
                    .hasEmailVerified(verifiedByEmail)
                    .hasPhoneVerified(verifiedByPhone)
                    .roles(List.of(roles))
                    .build();
            UserModel savedUser = usersServices.createUser(userModel);
            UserResponse response = UserResponseMapper.mapFromUser(savedUser);

            String token = firebaseService.createCustomToken(savedUser.getFirebaseUid());

            otpSessionService.deleteOtpSessionById(validatedSession.getId());

            return AuthResponse.builder().userResponse(response).token(token).build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to complete registration: " + e.getMessage());
        }
    }
}
