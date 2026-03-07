package com.mutrix.prepa.application.dto.commandes.matieres;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateMatieresDto {

    @NotBlank(message = "La matiere est obligatoire")
    @Schema(description = "Nom de la matiere", example = "Mathematics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description =  "description de la matiere", example = "Cours de mathematiques pour les prepas concours")
    private String description;
}

