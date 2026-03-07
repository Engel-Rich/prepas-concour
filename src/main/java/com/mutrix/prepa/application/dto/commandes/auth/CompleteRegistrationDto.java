package com.mutrix.prepa.application.dto.commandes.auth;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CompleteRegistrationDto {

    @NotNull(message = "OTP session ID is required")
    @Schema(description = "L'identifiant de la session OTP", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID otpSessionId;

    @NotNull(message = "OTP code is required")
    @Schema(description = "Le code OTP envoyé à l'utilisateur", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long otp;

    @NotNull(message = "Password is required")
    @Schema(description = "Le mot de passe choisi par l'utilisateur", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long password;
}
