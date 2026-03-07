package com.mutrix.prepa.application.dto.commandes.auth;

import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResendOtpCommand {

    @NotNull(message = "OTP session ID cannot be blank")
    @Schema(description = "L'identifiant de la session OTP pour laquelle le code doit être renvoyé", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID otpSessionId;

    @Schema(description = "Le type de notification pour lequel le code OTP doit être renvoyé (par exemple, SMS ou EMAIL)", example = "SMS", requiredMode = Schema.RequiredMode.REQUIRED)
    private NotificationType type;
}
