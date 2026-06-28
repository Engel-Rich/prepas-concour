package com.mutrix.prepa.application.dto.commandes.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordValidateDto {

    @NotNull(message = "L'identifiant de session OTP est requis")
    @Schema(description = "Identifiant de la session OTP reçu lors de l'initiation", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID otpSessionId;

    @NotBlank(message = "Le code OTP est requis")
    @Schema(description = "Code OTP à 6 chiffres reçu par l'utilisateur", example = "847291", requiredMode = Schema.RequiredMode.REQUIRED)
    private String otp;

    @NotBlank(message = "Le nouveau mot de passe est requis")
    @Schema(description = "Nouveau mot de passe (texte pour les admins, PIN numérique pour les utilisateurs)", example = "@NewPass123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
