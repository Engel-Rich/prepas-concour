package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.commandes.auth.*;
import com.mutrix.prepa.application.dto.response.AuthResponse;
import com.mutrix.prepa.application.dto.response.OtpResponse;
import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.application.usecases.auth.*;
import com.mutrix.prepa.cors.ApiResponseFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
@RequiredArgsConstructor
public class AuthController {
    private final InitiateLoginUseCase initiateLoginUseCase;
    private final RegistrationsInitiateUseCase registrationsInitiateUseCase;
    private final CompleteRegistrationUseCase completeRegistrationUseCase;
    private final AdminLoginUseCase adminLoginUseCase;
    private final CompleteLoginUseCase completeLoginUseCase;
    private final LoginByOAuth2ProviderUseCase loginByOAuth2ProviderUseCase;
    private final ResendOtpUseCase resendOtpUseCase;

    @Operation(summary = "Initialise la connexion", description = "Envoie un code OTP par email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP envoyé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou utilisateur introuvable"),
    })
    @PostMapping("/login/initiate")
    public ResponseEntity<ApiResponseFormat<OtpResponse>> initiateLogin(@RequestBody(required = true) @Valid LoginCommand command) {
        OtpResponse response = this.initiateLoginUseCase.execute(command);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    @Operation(summary = "Initialise l'inscription", description = "Envoie un code OTP par SMS pour l'inscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP envoyé avec succès", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OtpResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou utilisateur déjà existant")
    })
    @PostMapping("/register/initiate")
    public ResponseEntity<ApiResponseFormat<OtpResponse>> initiateRegistration(
            @RequestBody(required = true) @Valid RegisterCommand command) {
        OtpResponse response = this.registrationsInitiateUseCase.execute(command);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));

    }

    @Operation(summary = "Complète l'inscription", description = "Valide le code OTP et crée un compte utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription complétée avec succès", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou code OTP incorrect")
    })
    @PostMapping("/register/complete")
    public ResponseEntity<ApiResponseFormat<AuthResponse>> completeRegistration(
            @RequestBody(required = true) @Valid CompleteRegistrationDto command) {
        AuthResponse response = this.completeRegistrationUseCase.execute(command);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    @Operation(summary = "Complète la connexion", description = "Valide le code OTP et génère un token d'authentification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion complétée avec succès", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou code OTP incorrect")
    })
    @PostMapping("/login/complete")
    public ResponseEntity<ApiResponseFormat<AuthResponse>> completeLogin(@RequestBody(required = true) @Valid LoginCompletionDto command) {
        AuthResponse response = this.completeLoginUseCase.execute(command);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));

    }

    @Operation(summary = "Connexion via un fournisseur OAuth2", description = "Permet à l'utilisateur de se connecter en utilisant un fournisseur OAuth2 (Google, Facebook, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie via le fournisseur OAuth2", content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou échec de l'authentification avec le fournisseur OAuth2")
    })
    @PostMapping("/login/oauth2")
    public ResponseEntity<ApiResponseFormat<UserResponse>> loginByOAuth2Provider(
            @RequestBody(required = true) @Valid LoginOauth2Command command) {

        UserResponse response = this.loginByOAuth2ProviderUseCase.execute(command);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));

    }

    @Operation(summary = "Renvoyer le code OTP", description = "Permet de renvoyer un code OTP à l'utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OTP renvoyé avec succès", content = @Content(schema = @Schema(implementation = OtpResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides ou échec de l'envoi du code OTP")
    })
    @PostMapping("/otp/resend")
    public ResponseEntity<ApiResponseFormat<OtpResponse>> resendOtp(@RequestBody(required = true) @Valid ResendOtpCommand command) {
        OtpResponse response = this.resendOtpUseCase.execute(command);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    @Operation(summary = "Connexion administrateur", description = "Authentification par email et mot de passe pour les administrateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé — rôle ADMIN requis")
    })
    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponseFormat<AuthResponse>> adminLogin(
            @RequestBody(required = true) @Valid AdminLoginCommand command) {
        AuthResponse response = this.adminLoginUseCase.execute(command);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

}
