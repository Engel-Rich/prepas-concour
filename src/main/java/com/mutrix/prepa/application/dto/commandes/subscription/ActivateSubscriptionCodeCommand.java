package com.mutrix.prepa.application.dto.commandes.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload d'activation d'un code de souscription")
public class ActivateSubscriptionCodeCommand {

    @NotBlank(message = "Le code d'activation est obligatoire")
    @Schema(description = "Code d'activation reçu (tirets et casse ignorés)", example = "AB7K-9XMP")
    private String code;

    /**
     * Session que l'utilisateur pense activer. Renseignée quand l'activation
     * est lancée depuis la page d'un concours : le serveur refuse alors le code
     * s'il appartient à une autre session, sans le consommer.
     */
    @Schema(description = "Session de concours attendue — le code est rejeté s'il vise une autre session",
            nullable = true)
    private UUID concoursSessionId;
}
