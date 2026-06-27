package com.mutrix.prepa.application.dto.commandes.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de modification d'un utilisateur par l'administrateur")
public class UpdateUserAdminDto {

    @Schema(description = "Nom complet", example = "Jean Dupont")
    private String fullName;

    @Schema(description = "Adresse e-mail", example = "jean.dupont@example.com")
    private String email;

    @Schema(description = "Numéro de téléphone", example = "+237699000000")
    private String phone;

    @Schema(description = "Activer ou désactiver le compte", example = "true")
    private Boolean isActive;
}
