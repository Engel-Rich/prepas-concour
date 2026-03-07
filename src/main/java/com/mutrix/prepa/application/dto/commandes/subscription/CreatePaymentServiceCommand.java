package com.mutrix.prepa.application.dto.commandes.subscription;

import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload de création d'un fournisseur de paiement")
public class CreatePaymentServiceCommand {

    @NotBlank(message = "Le nom est obligatoire")
    @Schema(description = "Nom du service", example = "Orange Money CM")
    private String name;

    @Schema(description = "Description du service")
    private String description;

    @Schema(description = "URL du logo")
    private String logoUrl;

    @NotNull(message = "Le fournisseur est obligatoire")
    @Schema(description = "ID du fournisseur de paiement")
    private UUID paymentProviderId;

    @NotNull(message = "Le sens est obligatoire")
    @Schema(description = "Sens de la transaction", example = "DEBIT")
    private TransactionSens sens;

    @Schema(description = "Expression régulière de validation", example = "^237(69|65)[0-9]{7}$")
    private String regExp;

    @Schema(description = "Taux appliqué par le provider provider", example = "0.02")
    @NotNull(message = "Le taux du provider est obligatoire")
    private Double providerRate;

    @Schema(description = "Taux appliqué du le systeme", example = "0.02")
    private Double rate;
}
