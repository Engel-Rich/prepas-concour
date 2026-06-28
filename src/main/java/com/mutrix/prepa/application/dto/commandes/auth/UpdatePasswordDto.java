package com.mutrix.prepa.application.dto.commandes.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDto {

    @NotNull(message = "L'ancien mot de passe est requis")
    @Schema(description = "Ancien code PIN numérique", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long oldPassword;

    @NotNull(message = "Le nouveau mot de passe est requis")
    @Schema(description = "Nouveau code PIN numérique", example = "654321", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long newPassword;
}
