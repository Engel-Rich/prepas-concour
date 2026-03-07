package com.mutrix.prepa.application.dto.mappers;

import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.domaines.models.UserModel;

public final class UserResponseMapper {

    private   UserResponseMapper(){}

    public static UserResponse mapFromUser(UserModel user){
        return UserResponse.builder()
                .email(user.getEmail())
                .fullName(user.getName())
                .phone(user.getPhone())
                .firebaseUid(user.getFirebaseUid())
                .id(user.getId())
                .roles(user.getRoles())
                .hasPassword(user.hasPassWord())
                .hasEmailVerified(user.getHasEmailVerified())
                .hasPhoneVerified(user.getHasPhoneVerified())
                .hasProfileCompleted(user.hasProfileCompleted())
                .metadata(user.getMetadata())
                .build();
    }
}
