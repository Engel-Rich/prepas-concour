package com.mutrix.prepa.domaines.models;

import com.mutrix.prepa.domaines.valueobjects.UserRole;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Users {
    @Setter(AccessLevel.NONE)
    private UUID id;
    private String firebaseUid;
    private String name;
    private String email;
    private String phone;
    private String profilePictureUrl;
    private String fcmToken;
    private String passwordHash;
    private List<UserRole> roles = List.of(UserRole.USER);
    private Boolean isActive;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private Date lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;

    public Users(String firebaseUid, String phone, String fcmToken, List<UserRole> roles) {
        this.firebaseUid = firebaseUid;
        this.phone = phone;
        this.fcmToken = fcmToken;
        this.roles = roles;
    }

    public Users(String firebaseUid, String name, String email) {
        this.firebaseUid = firebaseUid;
        this.name = name;
        this.email = email;
    }
}

