package com.mutrix.prepa.domaines.services;

import com.mutrix.prepa.application.dto.commandes.users.CreateFirebaseUserDto;
import com.mutrix.prepa.domaines.models.FirebaseUser;
import com.mutrix.prepa.domaines.models.UserModel;

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

    /**
     * Synchronise un UserModel vers Firebase (email, phone, nom complet, photo, password).
     * Vérifie avant toute modification qu'aucun autre compte Firebase n'utilise
     * le même email ou numéro de téléphone que ceux portés par userModel.
     *
     * @param userModel     Données utilisateur à synchroniser
     * @param plainPassword Nouveau mot de passe en clair, ou {@code null} si inchangé
     * @return L'utilisateur Firebase mis à jour, ou {@code null} si firebaseUid est absent
     */
    public FirebaseUser syncUserToFirebase(UserModel userModel, String plainPassword);

}
