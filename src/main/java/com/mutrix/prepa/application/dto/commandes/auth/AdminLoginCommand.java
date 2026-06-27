package com.mutrix.prepa.application.dto.commandes.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Identifiants de connexion administrateur")
public class AdminLoginCommand {

    @Email(message = "Adresse email invalide")
    @NotBlank(message = "L'email est obligatoire")
    @Schema(description = "Email de l'administrateur", example = "admin@mutrix.org")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Schema(description = "Mot de passe de l'administrateur")
    private String password;
}
