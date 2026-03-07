package com.mutrix.prepa.presentation.subscription;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.application.usecases.subscriptions.DeleteSubscriptionUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.GetSubscriptionByIdUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.InitiateSubscriptionUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.SearchSubscriptionsUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.models.subscriptions.Subscription;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Créer une souscription", description = "Crée une nouvelle souscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Souscription créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
    })
    @PostMapping
    public ResponseEntity<ApiResponseFormat<SubscriptionResponse>> save(
            @RequestBody @Valid Subscription subscription) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponseCreate(initiateSubscriptionUseCase.execute(subscription)));
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

    @Operation(summary = "Lister toutes les souscriptions", description = "Retourne toutes les souscriptions paginées (admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(searchSubscriptionsUseCase.execute(page, size)));
    }

    @Operation(summary = "Rechercher les souscriptions d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(searchSubscriptionsUseCase.execute(userId, page, size)));
    }

    @Operation(summary = "Rechercher par utilisateur et statut")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listByUserAndStatus(
            @PathVariable String userId,
            @PathVariable SubscriptionStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(searchSubscriptionsUseCase.execute(userId, status, page, size)));
    }

    @Operation(summary = "Rechercher par utilisateur et session de concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    @GetMapping("/user/{userId}/session/{concoursSessionId}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listByUserAndSession(
            @PathVariable String userId,
            @PathVariable String concoursSessionId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(searchSubscriptionsUseCase.execute(userId, concoursSessionId, page, size)));
    }

    @Operation(summary = "Rechercher par utilisateur, session et statut")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    @GetMapping("/user/{userId}/session/{concoursSessionId}/status/{status}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listByUserSessionAndStatus(
            @PathVariable String userId,
            @PathVariable String concoursSessionId,
            @PathVariable SubscriptionStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(searchSubscriptionsUseCase.execute(userId, concoursSessionId, status, page, size)));
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