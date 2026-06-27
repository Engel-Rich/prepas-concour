package com.mutrix.prepa.application.dto.commandes.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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

    @NotNull(message = "L'ID du service de paiement est obligatoire")
    @Schema(description = "ID du service de paiement (Orange Money, MTN MoMo...)")
    private UUID paymentServiceId;

    @NotBlank(message = "Le numéro de téléphone est obligatoire")
    @Schema(description = "Numéro de téléphone pour le paiement mobile", example = "237699000000")
    private String phoneNumber;

    @Min(value = 1, message = "Le nombre de places doit être au minimum 1")
    @Schema(description = "Nombre de places", example = "1", defaultValue = "1")
    private Integer count = 1;

    private Map<String, Object> metadata;
}
