package com.mutrix.prepa.application.dto.commandes.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload de création d'une souscription")
public class CreateSubscriptionCommand {
    @NotNull(message = "La session de concours est obligatoire")
    @Schema(description = "ID de la session de concours")
    private UUID concoursSessionId;

    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom du fournisseur", example = "Orange Money")
    private String phoneNumber;

    private Map<String, Object> metadata;
}
