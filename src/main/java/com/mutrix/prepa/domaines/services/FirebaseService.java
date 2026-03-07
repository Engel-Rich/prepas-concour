package com.mutrix.prepa.domaines.services;

import com.mutrix.prepa.application.dto.commandes.users.CreateFirebaseUserDto;
import com.mutrix.prepa.domaines.models.FirebaseUser;

public interface FirebaseService {
    public FirebaseUser createUser(CreateFirebaseUserDto createFirebaseUserDto);

    public void deleteUser(String uid);

    public String createCustomToken (String uid);

    public FirebaseUser updateUser(String firebaseUid, CreateFirebaseUserDto updateFirebaseUserDto);

    public FirebaseUser getUserByUid(String uid);

    public FirebaseUser getUserByEmail(String email);

    public FirebaseUser getByPhoneNumber(String phoneNumber);

    public FirebaseUser verifyIdToken(String token);

    public void sendPasswordResetEmail(String email);

    public void sendEmailVerification(String uid);

}
