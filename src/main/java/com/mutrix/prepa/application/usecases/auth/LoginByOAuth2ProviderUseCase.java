package com.mutrix.prepa.application.usecases.auth;

import java.util.List;

import com.mutrix.prepa.application.dto.commandes.auth.LoginOauth2Command;
import com.mutrix.prepa.application.dto.mappers.UserResponseMapper;
import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.models.FirebaseUser;
import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.interfaces.RolesServices;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.services.FirebaseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@UseCase
public class LoginByOAuth2ProviderUseCase {
    private final FirebaseService firebaseService;
    private final UsersServices usersServices;
    private final RolesServices rolesServices;

    public UserResponse execute(LoginOauth2Command dto) {
        try {
            final Roles roles = rolesServices.getRoleByName("USER");
            FirebaseUser firebaseUser = firebaseService.verifyIdToken(dto.getOAuth2ProviderToken());
            var userOpt = usersServices.getUserByFirebaseUid(firebaseUser.getUid());
            if (userOpt.isEmpty()) {
                UserModel newUser = getUsersFromFirebaseUser(firebaseUser, roles);
                UserModel savedUser = usersServices.createUser(newUser);
                return UserResponseMapper.mapFromUser(savedUser);
            }
            UserModel newUser = getNewUser(userOpt.get(), firebaseUser);
            UserModel updatedUser = usersServices.updateUser(newUser);
            return UserResponseMapper.mapFromUser(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to login with OAuth2 provider: " + e.getMessage());
        }
    }

    private static UserModel getNewUser(UserModel userOpt, FirebaseUser firebaseUser) {
        userOpt.setEmail(firebaseUser.getEmail() != null ? firebaseUser.getEmail() : userOpt.getEmail());
        userOpt.setPhone(firebaseUser.getPhoneNumber() != null ? firebaseUser.getPhoneNumber() : userOpt.getPhone());
        userOpt.setProfilePictureUrl(
                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl() : userOpt.getProfilePictureUrl());
        userOpt.setName(userOpt.getName() == null || userOpt.getName().isEmpty() ? firebaseUser.getDisplayName()
                : userOpt.getName());
        userOpt.setMetadata(firebaseUser.getMetadata());
        return userOpt;
    }

    private static UserModel getUsersFromFirebaseUser(FirebaseUser firebaseUser, Roles role) {
        return UserModel.builder()
                .email(firebaseUser.getEmail())
                .name(firebaseUser.getDisplayName())
                .phone(firebaseUser.getPhoneNumber())
                .firebaseUid(firebaseUser.getUid())
                .metadata(firebaseUser.getMetadata())
                .hasEmailVerified(firebaseUser.getEmailVerified())
                .hasPhoneVerified(firebaseUser.getPhoneNumber() != null && !firebaseUser.getPhoneNumber().isEmpty())
                .roles(List.of(role))
                .build();
    }
}
