package com.mutrix.prepa.presentation.subscription;

import com.mutrix.prepa.application.dto.commandes.subscription.CreateSubscriptionCommand;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.application.usecases.subscriptions.DeleteSubscriptionUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.GetSubscriptionByIdUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.InitiateSubscriptionUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.SearchSubscriptionsUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Souscriptions", description = "API de gestion des souscriptions")
public class SubscriptionController {

    private final InitiateSubscriptionUseCase initiateSubscriptionUseCase;
    private final GetSubscriptionByIdUseCase getSubscriptionByIdUseCase;
    private final SearchSubscriptionsUseCase searchSubscriptionsUseCase;
    private final DeleteSubscriptionUseCase deleteSubscriptionUseCase;

    private UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((SecurityUser) auth.getPrincipal()).getUser().getId();
    }

    @Operation(summary = "Créer une souscription",
            description = "Initie une souscription à une session de concours. Crée la souscription en statut INITIATE et déclenche le paiement mobile auprès du fournisseur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Souscription initiée, paiement en attente"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Session ou service de paiement introuvable"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
    })
    @PostMapping
    public ResponseEntity<ApiResponseFormat<SubscriptionResponse>> initiate(
            @RequestBody @Valid CreateSubscriptionCommand command) {
        UUID userId = getAuthenticatedUserId();
        SubscriptionResponse response = initiateSubscriptionUseCase.execute(command, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponseCreate(response));
    }

    @Operation(summary = "Obtenir une souscription par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Souscription trouvée"),
            @ApiResponse(responseCode = "404", description = "Souscription introuvable"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<SubscriptionResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getSubscriptionByIdUseCase.execute(id)));
    }

    @Operation(summary = "Lister mes souscriptions", description = "Retourne les souscriptions de l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listMine(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        UUID userId = getAuthenticatedUserId();
        return ResponseEntity.ok(ApiResponseFormat.fromPage(
                searchSubscriptionsUseCase.execute(userId.toString(), page, size)));
    }

    @Operation(summary = "Mes souscriptions filtrées par statut")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    @GetMapping("/me/status/{status}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listMineByStatus(
            @PathVariable SubscriptionStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        UUID userId = getAuthenticatedUserId();
        return ResponseEntity.ok(ApiResponseFormat.fromPage(
                searchSubscriptionsUseCase.execute(userId.toString(), status, page, size)));
    }

    @Operation(summary = "Supprimer une souscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Souscription supprimée"),
            @ApiResponse(responseCode = "404", description = "Souscription introuvable"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteSubscriptionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
