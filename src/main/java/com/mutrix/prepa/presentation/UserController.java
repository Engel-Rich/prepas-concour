package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.commandes.auth.UpdatePasswordDto;
import com.mutrix.prepa.application.dto.commandes.users.SetPasswordDto;
import com.mutrix.prepa.application.dto.commandes.users.UpdateFcmTokenDto;
import com.mutrix.prepa.application.dto.commandes.users.UpdateProfileDto;
import com.mutrix.prepa.application.dto.mappers.UserResponseMapper;
import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.application.usecases.auth.UpdatePasswordUseCase;
import com.mutrix.prepa.application.usecases.users.SetPasswordUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
import com.mutrix.prepa.infrastructure.services.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Opérations sur le compte de l'utilisateur connecté")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UpdatePasswordUseCase updatePasswordUseCase;
    private final SetPasswordUseCase    setPasswordUseCase;
    private final UsersServices         usersServices;
    private final MinioService          minioService;

    // ── Profil ────────────────────────────────────────────────────────────────

    @Operation(summary = "Récupérer son profil")
    @GetMapping("/me")
    public ResponseEntity<ApiResponseFormat<UserResponse>> getMe(
            @AuthenticationPrincipal SecurityUser securityUser) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                UserResponseMapper.mapFromUser(securityUser.getUser())));
    }

    @Operation(summary = "Mettre à jour son profil (nom, téléphone)")
    @PutMapping("/me")
    public ResponseEntity<ApiResponseFormat<UserResponse>> updateProfile(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody UpdateProfileDto dto) {

        UserModel user = securityUser.getUser();
        if (dto.getFullName()    != null && !dto.getFullName().isBlank())    user.setName(dto.getFullName());
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) user.setPhone(dto.getPhoneNumber());

        UserModel saved = usersServices.updateUser(user, null);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                UserResponseMapper.mapFromUser(saved)));
    }

    @Operation(summary = "Uploader sa photo de profil")
    @PostMapping(value = "/me/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<UserResponse>> uploadPicture(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestParam("file") MultipartFile file) {

        String url  = minioService.uploadFile(file, "profiles");
        UserModel user = securityUser.getUser();
        user.setProfilePictureUrl(url);

        UserModel saved = usersServices.updateUser(user, null);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                UserResponseMapper.mapFromUser(saved)));
    }

    // ── FCM Token ──────────────────────────────────────────────────────────────

    @Operation(summary = "Enregistrer / mettre à jour le token FCM")
    @PutMapping("/fcm-token")
    public ResponseEntity<ApiResponseFormat<Void>> updateFcmToken(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody @Valid UpdateFcmTokenDto dto) {

        UserModel user = securityUser.getUser();
        user.setFcmToken(dto.getFcmToken());
        usersServices.updateUser(user, null);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(null));
    }

    // ── Mot de passe ────────────────────────────────────────────────────────────

    @Operation(summary = "Définir un mot de passe pour un compte OAuth2 (sans mot de passe)")
    @PutMapping("/me/set-password")
    public ResponseEntity<ApiResponseFormat<Void>> setPassword(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody @Valid SetPasswordDto dto) {

        setPasswordUseCase.execute(securityUser.getUser(), dto);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(null));
    }

    @Operation(summary = "Mettre à jour le mot de passe (PIN)")
    @PutMapping("/password/update")
    public ResponseEntity<ApiResponseFormat<Void>> updatePassword(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody @Valid UpdatePasswordDto dto) {

        if (securityUser == null) throw new RuntimeException("Utilisateur non authentifié");
        updatePasswordUseCase.execute(securityUser.getUser(), dto);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(null));
    }
}
