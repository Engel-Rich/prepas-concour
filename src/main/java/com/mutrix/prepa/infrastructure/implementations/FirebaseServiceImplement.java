package com.mutrix.prepa.infrastructure.implementations;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserRecord;
import com.mutrix.prepa.application.dto.commandes.users.CreateFirebaseUserDto;
import com.mutrix.prepa.domaines.models.FirebaseUser;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.services.FirebaseService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FirebaseServiceImplement implements FirebaseService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseServiceImplement.class);

    @Override
    public FirebaseUser createUser(CreateFirebaseUserDto createFirebaseUserDto) {
        try {
            final UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmailVerified(createFirebaseUserDto.getEmailVerified() != null
                            && createFirebaseUserDto.getEmailVerified())
                    .setPassword(createFirebaseUserDto.getPassword());
            if (createFirebaseUserDto.getPhotoUrl() != null && !createFirebaseUserDto.getPhotoUrl().isEmpty()) {
                request.setPhotoUrl(createFirebaseUserDto.getPhotoUrl());
            }
            if (createFirebaseUserDto.getPassword() != null && !createFirebaseUserDto.getPassword().isEmpty()) {
                request.setPassword(createFirebaseUserDto.getPassword());
            }
            if (createFirebaseUserDto.getDisplayName() != null && !createFirebaseUserDto.getDisplayName().isEmpty()) {
                request.setDisplayName(createFirebaseUserDto.getDisplayName());
            }
            if (createFirebaseUserDto.getPhoneNumber() != null && !createFirebaseUserDto.getPhoneNumber().isEmpty()) {
                String phone = createFirebaseUserDto.getPhoneNumber().startsWith("+237") ? createFirebaseUserDto.getPhoneNumber():"+237"+createFirebaseUserDto.getPhoneNumber().trim();
                request.setPhoneNumber(phone);
            }
            if (createFirebaseUserDto.getEmail() != null && !createFirebaseUserDto.getEmail().isEmpty()) {
                request.setEmail(createFirebaseUserDto.getEmail());
            }
            final UserRecord record = FirebaseAuth.getInstance().createUser(request);
            return this.mapToFirebaseUser(record);
        } catch (FirebaseException exception) {
            System.out.println(exception.getErrorCode());
            log.error("Firebase Error creating user: {}, Code : {}", exception.getMessage(), exception.getErrorCode());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.error("Auther Error creating user: {}, Error type {}", e.getMessage(), e.getClass().toString());
        }
        return null;
    }

    @Override
    public void deleteUser(String uid) {
        try {
            FirebaseAuth.getInstance().deleteUser(uid);
        } catch (FirebaseAuthException e) {
            log.error("Error deleting user: {}, Code : {}", e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage());
        }
    }

    @Override
    public String createCustomToken(String uid) {
        try {
            return FirebaseAuth.getInstance().createCustomToken(uid);
        } catch (Exception e) {
            log.error("Error creating custom token: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public FirebaseUser updateUser(String uid, CreateFirebaseUserDto dto) {
        try {
            String phone = dto.getPhoneNumber()==null? null: dto.getPhoneNumber().startsWith("+237") ? dto.getPhoneNumber():"+237"+dto.getPhoneNumber().trim();
            final UserRecord record = FirebaseAuth.getInstance().getUser(uid);
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                    .setDisplayName(dto.getDisplayName() != null ? dto.getDisplayName() : record.getDisplayName())
                    .setEmail(dto.getEmail() != null ? dto.getEmail() : record.getEmail())
                    .setPhoneNumber(phone != null ? phone : record.getPhoneNumber())
                    .setPhotoUrl(dto.getPhotoUrl() != null ? dto.getPhotoUrl() : record.getPhotoUrl())
                    .setEmailVerified(dto.getEmailVerified() != null ? dto.getEmailVerified() : record.isEmailVerified())
                    .setDisabled(dto.getDisabled() != null ? dto.getDisabled() : record.isDisabled());
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                request.setPassword(dto.getPassword());
            }
            UserRecord updated = FirebaseAuth.getInstance().updateUser(request);
            return this.mapToFirebaseUser(updated);
        } catch (FirebaseException e) {
            log.error("Error updating user: {}, Code : {}", e.getMessage(), e.getErrorCode());
            return null;
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public FirebaseUser syncUserToFirebase(UserModel userModel, String plainPassword) {
        if (userModel.getFirebaseUid() == null) return null;
        try {
            UserRecord current = FirebaseAuth.getInstance().getUser(userModel.getFirebaseUid());

            // Vérifier l'unicité de l'email sur Firebase avant toute modification locale
            if (userModel.getEmail() != null && !userModel.getEmail().equals(current.getEmail())) {
                try {
                    UserRecord byEmail = FirebaseAuth.getInstance().getUserByEmail(userModel.getEmail());
                    if (!byEmail.getUid().equals(userModel.getFirebaseUid())) {
                        throw new IllegalArgumentException(
                                "Cet email est déjà utilisé par un autre compte : " + userModel.getEmail());
                    }
                } catch (FirebaseAuthException e) {
                    if (e.getAuthErrorCode() != AuthErrorCode.USER_NOT_FOUND) {
                        throw new RuntimeException("Erreur Firebase lors de la vérification de l'email : " + e.getMessage());
                    }
                    // USER_NOT_FOUND → email disponible, on continue
                }
            }

            // Vérifier l'unicité du numéro de téléphone sur Firebase avant toute modification locale
            if (userModel.getPhone() != null && !userModel.getPhone().equals(current.getPhoneNumber())) {
                try {
                    UserRecord byPhone = FirebaseAuth.getInstance().getUserByPhoneNumber(userModel.getPhone());
                    if (!byPhone.getUid().equals(userModel.getFirebaseUid())) {
                        throw new IllegalArgumentException(
                                "Ce numéro de téléphone est déjà utilisé par un autre compte : " + userModel.getPhone());
                    }
                } catch (FirebaseAuthException e) {
                    if (e.getAuthErrorCode() != AuthErrorCode.USER_NOT_FOUND) {
                        throw new RuntimeException("Erreur Firebase lors de la vérification du téléphone : " + e.getMessage());
                    }
                    // USER_NOT_FOUND → numéro disponible, on continue
                }
            }

            // Construire la requête de mise à jour Firebase
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(userModel.getFirebaseUid());
            if (userModel.getName() != null) request.setDisplayName(userModel.getName());
            if (userModel.getEmail() != null) request.setEmail(userModel.getEmail());
            if (userModel.getPhone() != null) request.setPhoneNumber(userModel.getPhone());
            if (userModel.getProfilePictureUrl() != null) request.setPhotoUrl(userModel.getProfilePictureUrl());
            if (plainPassword != null && !plainPassword.isBlank()) request.setPassword(plainPassword);

            UserRecord updated = FirebaseAuth.getInstance().updateUser(request);
            return mapToFirebaseUser(updated);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (FirebaseAuthException e) {
            log.error("Firebase error syncing user {}: {} (code={})", userModel.getFirebaseUid(), e.getMessage(), e.getAuthErrorCode());
            throw new RuntimeException("Erreur Firebase lors de la synchronisation : " + e.getMessage());
        } catch (Exception e) {
            log.error("Error syncing user {} to Firebase: {}", userModel.getFirebaseUid(), e.getMessage());
            throw new RuntimeException("Erreur lors de la synchronisation avec Firebase : " + e.getMessage());
        }
    }

    @Override
    public FirebaseUser getUserByUid(String uid) {
        try {
            final UserRecord record = FirebaseAuth.getInstance().getUser(uid);
            return this.mapToFirebaseUser(record);
        } catch (FirebaseException e) {
            log.error("Error fetching user: {}, Code : {}", e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Optional<FirebaseUser> getUserByEmail(String email) {
        try {
            final UserRecord record = FirebaseAuth.getInstance().getUserByEmail(email);
            return Optional.of(this.mapToFirebaseUser(record));
        } catch (FirebaseException e) {
            log.error("Error fetching user: {}, Code : {}", e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<FirebaseUser> getByPhoneNumber(String phoneNumber) {
        try {
            final UserRecord record = FirebaseAuth.getInstance().getUserByPhoneNumber(phoneNumber);
            return Optional.of(this.mapToFirebaseUser(record));
        } catch (FirebaseException e) {
            log.error("Error fetching user: {}, Code : {}", e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public FirebaseUser verifyIdToken(String token) {
        try {
            // Vérifie la signature et l'expiration — appel réseau pour récupérer
            // les clés publiques Google (mis en cache après le premier appel)
            final FirebaseToken verified = FirebaseAuth.getInstance().verifyIdToken(token);

            // On construit FirebaseUser DIRECTEMENT depuis les claims du token vérifié,
            // sans faire un deuxième appel réseau getUser() qui peut échouer silencieusement.
            return FirebaseUser.builder()
                    .uid(verified.getUid())
                    .email(verified.getEmail())
                    .displayName(verified.getName())
                    .photoUrl(verified.getPicture())
                    .emailVerified(verified.isEmailVerified())
                    .build();

        } catch (com.google.firebase.auth.FirebaseAuthException e) {
            log.error("Échec de vérification du ID Token Firebase : {} (code={})",
                      e.getMessage(), e.getAuthErrorCode());
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la vérification du ID Token : {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void sendPasswordResetEmail(String email) {
        try {
            FirebaseAuth.getInstance().generatePasswordResetLink(email);
            // Send Link By email or phone number using notification service
        } catch (FirebaseException e) {
            log.error("Error sending password reset email: {}, Code : {}", e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            log.error("Error sending password reset email: {}", e.getMessage());
        }
    }

    @Override
    public void sendEmailVerification(String uid) {
        try {
            FirebaseAuth.getInstance()
                    .generateEmailVerificationLink(FirebaseAuth.getInstance().getUser(uid).getEmail());
            // Send Link By email using notification service
        } catch (FirebaseException e) {
            log.error("Error sending email verification: {}, Code : {}", e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            log.error("Error sending email verification: {}", e.getMessage());
        }
    }

    private FirebaseUser mapToFirebaseUser(UserRecord record) {
        return FirebaseUser.builder()
                .uid(record.getUid())
                .email(record.getEmail())
                .displayName(record.getDisplayName())
                .photoUrl(record.getPhotoUrl())
                .phoneNumber(record.getPhoneNumber())
                .emailVerified(record.isEmailVerified())
                .disabled(record.isDisabled())

                .metadata(Map.of(
                        "creationTime", record.getUserMetadata().getCreationTimestamp(),
                        "lastSignInTime", record.getUserMetadata().getLastSignInTimestamp(),
                        "LoginProviders",
                        Arrays.stream(record.getProviderData()).map(UserInfo::getDisplayName).toList()))
                .build();
    }
}
