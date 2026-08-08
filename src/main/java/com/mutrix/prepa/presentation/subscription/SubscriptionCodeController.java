package com.mutrix.prepa.presentation.subscription;

import com.mutrix.prepa.application.dto.commandes.subscription.ActivateSubscriptionCodeCommand;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeGroupResponse;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeResponse;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.application.usecases.subscriptions.codes.ActivateSubscriptionCodeUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.codes.GetMySubscriptionCodesUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/subscription-codes")
@RequiredArgsConstructor
@Tag(name = "Codes de souscription", description = "Achat groupé : codes d'activation partageables")
public class SubscriptionCodeController {

    private final GetMySubscriptionCodesUseCase getMySubscriptionCodesUseCase;
    private final ActivateSubscriptionCodeUseCase activateSubscriptionCodeUseCase;

    private UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((SecurityUser) auth.getPrincipal()).getUser().getId();
    }

    @Operation(summary = "Mes codes regroupés par concours",
            description = "Retourne les codes achetés par l'utilisateur connecté, regroupés par session de concours, "
                    + "avec le détail de chaque code (statut, date d'utilisation, activateur).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponseFormat<List<SubscriptionCodeGroupResponse>>> listMineGrouped() {
        UUID userId = getAuthenticatedUserId();
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                getMySubscriptionCodesUseCase.executeGrouped(userId)));
    }

    @Operation(summary = "Mes codes (liste à plat)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
    })
    @GetMapping("/me/flat")
    public ResponseEntity<ApiResponseFormat<List<SubscriptionCodeResponse>>> listMineFlat() {
        UUID userId = getAuthenticatedUserId();
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                getMySubscriptionCodesUseCase.executeFlat(userId)));
    }

    @Operation(summary = "Activer un code",
            description = "Consomme un code d'activation au profit de l'utilisateur connecté et lui crée "
                    + "une souscription active sur le concours associé. Un code n'est utilisable qu'une seule fois.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code activé, souscription créée"),
            @ApiResponse(responseCode = "404", description = "Code d'activation invalide"),
            @ApiResponse(responseCode = "409", description = "Code déjà utilisé ou accès déjà actif"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
    })
    @PostMapping("/activate")
    public ResponseEntity<ApiResponseFormat<SubscriptionResponse>> activate(
            @RequestBody @Valid ActivateSubscriptionCodeCommand command) {
        UUID userId = getAuthenticatedUserId();
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                activateSubscriptionCodeUseCase.execute(command, userId)));
    }
}
