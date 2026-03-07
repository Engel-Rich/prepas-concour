package com.mutrix.prepa.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ConcourResponseDTO", description = "Représentation d’un concours")
public class ConcourResponseDTO {

    @Schema(description = "Identifiant du concours")
    private UUID id;

    @Schema(description = "Nom du concours", example = "Concours ENAM")
    private String name;

    @Schema(description = "Description du concours")
    private String description;

    @Schema(description = "URL du logo du concours")
    private String logoUrl;

    @Schema(description = "Indique si le concours est actif")
    private Boolean isActive;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière modification")
    private LocalDateTime updatedAt;

    @Schema(description = "Informations supplémentaires")
    private Map<String, Object> metadata;
}
