package com.mutrix.prepa.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Champs communs à tous les modèles")
public class BaseResponse {

    @Schema(description = "Identifiant unique", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Date de création")
    private LocalDateTime createdAt;

    @Schema(description = "Date de dernière mise à jour")
    private LocalDateTime updatedAt;

    @Schema(description = "Statut actif/inactif")
    private Boolean isActive;

    @Schema(description = "Métadonnées additionnelles")
    private Map<String, Object> metadata;
}