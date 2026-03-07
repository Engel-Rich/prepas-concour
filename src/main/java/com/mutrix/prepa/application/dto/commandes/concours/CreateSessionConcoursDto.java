package com.mutrix.prepa.application.dto.commandes.concours;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateSessionConcoursDto {

    @NotNull(message = "L'ID du concours est obligatoire")
    private UUID concoursId;

    @NotBlank(message = "Mai 2021")
    @Schema(description = "Nom du concours", example = "Mathematics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description =  "description du concours", example = "Concours de la gendarmerie session de Mai")
    private String description;


    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate endDate;

    @Schema(
            description = "Informations supplémentaires sous format JSON",
            example = "{\"lieu\":\"Yaoundé\", \"capacite\":200}"
    )
    private Map<String, Object> metadata;

}
