package com.mutrix.prepa.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Matière rattachée à une session de concours")
public class MatiereSessionDto {

    @Schema(description = "Identifiant de la matière")
    private UUID id;

    @Schema(description = "Nom de la matière", example = "Mathématiques")
    private String name;

    @Schema(description = "URL du logo de la matière")
    private String logoUrl;

    @Schema(description = "Durée de l'épreuve en minutes", example = "240")
    private Integer dureeMinutes;

    @Schema(description = "Coefficient de la matière", example = "5.0")
    private Double coefficient;
}
