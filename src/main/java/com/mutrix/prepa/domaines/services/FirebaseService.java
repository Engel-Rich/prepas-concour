package com.mutrix.prepa.domaines.services;

import com.mutrix.prepa.application.dto.commandes.users.CreateFirebaseUserDto;
import com.mutrix.prepa.domaines.models.FirebaseUser;
import com.mutrix.prepa.domaines.models.UserModel;

import java.util.Optional;

public interface FirebaseService {
    public FirebaseUser createUser(CreateFirebaseUserDto createFirebaseUserDto);

    public void deleteUser(String uid);

    public String createCustomToken (String uid);

    public FirebaseUser updateUser(String firebaseUid, CreateFirebaseUserDto updateFirebaseUserDto);

    public FirebaseUser getUserByUid(String uid);

    public Optional<FirebaseUser> getUserByEmail(String email);

    public Optional<FirebaseUser> getByPhoneNumber(String phoneNumber);

    /**
     * Vérifie un ID Token Firebase, <b>révocation comprise</b>.
     * Un token émis avant un {@link #revokeRefreshTokens(String)} est rejeté.
     */
    public FirebaseUser verifyIdToken(String token);

    /**
     * Révoque tous les refresh tokens du compte : les sessions ouvertes sur les
     * autres appareils deviennent invalides dès leur prochain renouvellement.
     */
    public void revokeRefreshTokens(String uid);

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
