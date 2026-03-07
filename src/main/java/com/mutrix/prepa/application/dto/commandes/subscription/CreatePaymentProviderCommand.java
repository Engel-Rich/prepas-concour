package com.mutrix.prepa.application.dto.commandes.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload de création d'un fournisseur de paiement")
public class CreatePaymentProviderCommand {

    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom du fournisseur", example = "Orange Money")
    private String name;

    @Schema(description = "Description du fournisseur")
    private String description;

    @Schema(description = "URL du logo")
    private String logoUrl;
}
