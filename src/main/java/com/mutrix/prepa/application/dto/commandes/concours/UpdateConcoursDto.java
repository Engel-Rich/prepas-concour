package com.mutrix.prepa.application.dto.commandes.concours;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateConcoursDto {
    @Schema(description = "Nom du concour", example = "Mathematics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description =  "description du concour", example = "Concours d'entree a la gendarmerie nationale")
    private String description;

    @Schema(description = "etat du concour", example = "true")
    private Boolean isActive;

    @Schema(
            description = "Informations supplémentaires sous format JSON",
            example = "{\"lieu\":\"Yaoundé\", \"capacite\":200}"
    )
    private Map<String, Object> metadata;
}
