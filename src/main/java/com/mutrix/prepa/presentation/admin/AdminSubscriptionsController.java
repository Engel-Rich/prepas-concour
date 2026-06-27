package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.application.usecases.subscriptions.DeleteSubscriptionUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.GetSubscriptionByIdUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.SearchSubscriptionsUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.valueobjects.SubscriptionStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Admin - Inscriptions", description = "Gestion des inscriptions par l'administrateur")
public class AdminSubscriptionsController {

    private final GetSubscriptionByIdUseCase getSubscriptionByIdUseCase;
    private final SearchSubscriptionsUseCase searchSubscriptionsUseCase;
    private final DeleteSubscriptionUseCase deleteSubscriptionUseCase;

    @Operation(summary = "Lister toutes les inscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(searchSubscriptionsUseCase.execute(page, size)));
    }

    @Operation(summary = "Récupérer une inscription par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription trouvée"),
            @ApiResponse(responseCode = "404", description = "Inscription introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<SubscriptionResponse>> getById(
            @Parameter(description = "UUID de l'inscription") @PathVariable String id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getSubscriptionByIdUseCase.execute(id)));
    }

    @Operation(summary = "Inscriptions d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listByUser(
            @Parameter(description = "UUID de l'utilisateur") @PathVariable String userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(searchSubscriptionsUseCase.execute(userId, page, size)));
    }

    @Operation(summary = "Inscriptions d'un utilisateur filtrées par statut")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listByUserAndStatus(
            @Parameter(description = "UUID de l'utilisateur") @PathVariable String userId,
            @Parameter(description = "Statut de l'inscription") @PathVariable SubscriptionStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(searchSubscriptionsUseCase.execute(userId, status, page, size)));
    }

    @Operation(summary = "Inscriptions d'un utilisateur pour une session de concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/user/{userId}/session/{sessionId}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> listByUserAndSession(
            @Parameter(description = "UUID de l'utilisateur") @PathVariable String userId,
            @Parameter(description = "UUID de la session") @PathVariable String sessionId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(searchSubscriptionsUseCase.execute(userId, sessionId, page, size)));
    }

    @Operation(summary = "Supprimer une inscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscription supprimée"),
            @ApiResponse(responseCode = "404", description = "Inscription introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID de l'inscription") @PathVariable UUID id) {
        deleteSubscriptionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
