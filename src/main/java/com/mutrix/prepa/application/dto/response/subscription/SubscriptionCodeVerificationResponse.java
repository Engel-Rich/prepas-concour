package com.mutrix.prepa.application.dto.response.subscription;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/** Résultat du contrôle de cohérence d'un code d'activation. */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Diagnostic et réparation d'un code de souscription")
public class SubscriptionCodeVerificationResponse {

    @Schema(description = "Issue du contrôle")
    public enum Outcome {
        /** Toutes les conditions sont réunies — aucune intervention. */
        VALID,
        /** Code marqué utilisé sans activation réelle : remis à disposition. */
        RELEASED,
        /** Code rattaché à un achat non honoré : rendu inutilisable. */
        INVALIDATED,
        /** Session de concours close : code expiré. */
        EXPIRED,
        /** Code déjà consommé, activation cohérente — rien à corriger. */
        USED_CONSISTENT,
    }

    @Schema(description = "Issue du contrôle")
    private Outcome outcome;

    @Schema(description = "Message lisible destiné à l'opérateur")
    private String message;

    @Schema(description = "Contrôles effectués et leur résultat")
    private List<String> checks;

    @Schema(description = "Vrai si le contrôle a modifié le code")
    private Boolean changed;

    @Schema(description = "État du code après contrôle")
    private SubscriptionCodeResponse code;

    @Schema(description = "Code de remplacement généré, le cas échéant")
    private SubscriptionCodeResponse replacement;
}
