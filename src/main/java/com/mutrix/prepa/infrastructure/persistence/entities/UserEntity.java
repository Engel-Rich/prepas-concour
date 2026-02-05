package com.mutrix.prepa.infrastructure.persistence.entities;

import com.mutrix.prepa.domaines.valueobjects.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, name = "firebaseuid")
    private String firebaseUid;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Column(name = "profilepictureurl")
    private String profilePictureUrl;

    @Column(name = "fcmtoken")
    private String fcmToken;

    @Column(name = "passwordhash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "roles", nullable = false)
    private List<UserRole> roles = List.of(UserRole.USER);

    @Column(name = "isactive")
    private Boolean isActive;

    @Column(name = "hasemailverified")
    private Boolean isEmailVerified;

    @Column(name = "hasphoneverified")
    private Boolean isPhoneVerified;

    @Column(name = "lastlogin")
    private Date lastLogin;

    @Column(name = "createdat")
    private LocalDateTime createdAt;

    @Column(name = "updatedat")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "jsonb")
    private String metadata;



    @PostUpdate
    public  void onUpdate(){
        this.updatedAt= LocalDateTime.now();
    }

    @PostPersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
