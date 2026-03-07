package com.mutrix.prepa.application.usecases.auth;

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
                    String.valueOf(dto.getOtp()));
            CreateFirebaseUserDto createFirebaseUserDto = CreateFirebaseUserDto.builder()
                    .email(validatedSession.getEmail())
                    .password(dto.getPassword().toString())
                    .phoneNumber(validatedSession.getPhone())
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
