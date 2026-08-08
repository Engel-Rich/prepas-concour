package com.mutrix.prepa.application.dto.commandes.users;

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
public class SetPasswordDto {

    @NotNull(message = "Le mot de passe est requis")
    @Schema(description = "Code PIN numérique à définir", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long newPassword;
}
