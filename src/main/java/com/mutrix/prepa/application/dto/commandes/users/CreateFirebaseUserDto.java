package com.mutrix.prepa.application.dto.commandes.users;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFirebaseUserDto {
    private String email;
    private String displayName;
    private String photoUrl;
    private String phoneNumber;
    private Boolean emailVerified;
    private Boolean disabled;
    private  String password;
}
