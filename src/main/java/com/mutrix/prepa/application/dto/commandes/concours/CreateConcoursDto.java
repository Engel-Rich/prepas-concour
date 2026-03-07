package com.mutrix.prepa.application.dto.commandes.concours;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateConcoursDto {


    @NotBlank(message = "Le nom du concours est obligatoire")
    @Schema(description = "Nom du concours", example = "Mathematics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description =  "description du concours", example = "Concours d'entree a la gendarmerie nationale")
    private String description;

    @Schema(
            description = "Informations supplémentaires sous format JSON",
            example = "{\"lieu\":\"Yaoundé\", \"capacite\":200}"
    )
    private Map<String, Object> metadata;
}
