package com.mutrix.prepa.domaines.models;

import lombok.*;

import java.util.Map;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseUser {
    private  String uid;
    private  String email;
    private  String displayName;
    private  String photoUrl;
    private  String phoneNumber;
    private  Boolean emailVerified;
    private  Boolean disabled;
    private Map<String , Object> metadata;
}
