package com.mutrix.prepa.domaines.models;

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
public class UserModel {
    @Setter(AccessLevel.NONE)
    private UUID id;
    private String firebaseUid;
    private String name;
    private String email;
    private String phone;
    private String profilePictureUrl;
    private String fcmToken;
    private String passwordHash;
    private List<Roles> roles;
    private Boolean isActive;
    private Boolean hasEmailVerified;
    private Boolean hasPhoneVerified;
    private Date lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;

    public UserModel(String firebaseUid, String phone, String fcmToken, List<Roles> roles) {
        this.firebaseUid = firebaseUid;
        this.phone = phone;
        this.fcmToken = fcmToken;
        this.roles = roles;
    }

    public UserModel(String firebaseUid, String name, String email) {
        this.firebaseUid = firebaseUid;
        this.name = name;
        this.email = email;
    }

    public boolean hasPassWord() {
        return this.passwordHash != null && !this.passwordHash.isEmpty();
    }

    public boolean hasProfileCompleted() {
        return this.name != null && !this.name.isEmpty() &&
                this.email != null && !this.email.isEmpty() &&
                this.phone != null && !this.phone.isEmpty();
    }
}
