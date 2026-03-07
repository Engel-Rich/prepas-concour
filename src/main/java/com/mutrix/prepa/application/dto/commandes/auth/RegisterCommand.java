package com.mutrix.prepa.application.dto.commandes.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterCommand {

    @NotBlank(message = "Phone number is required")
    @NotNull(message = "Phone number cannot be null")
    @Schema(description = "Le numéro de téléphone de l'utilisateur", example = "+237 699 123 456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Schema(description = "email du user", example = "engel@rich.dev", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Full name is required")
    @NotNull(message = "Full name cannot be null")
    @Schema(description = "Le nom complet de l'utilisateur", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fullName;
}
