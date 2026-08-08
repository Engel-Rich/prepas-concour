package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeResponse;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionCodeVerificationResponse;
import com.mutrix.prepa.application.usecases.subscriptions.codes.ReissueSubscriptionCodeUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.codes.SearchSubscriptionCodesUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.codes.VerifySubscriptionCodeUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.valueobjects.CodeStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/subscription-codes")
@RequiredArgsConstructor
@Tag(name = "Admin - Codes de souscription", description = "Suivi des codes d'activation par l'administrateur")
public class AdminSubscriptionCodesController {

    private final SearchSubscriptionCodesUseCase searchSubscriptionCodesUseCase;
    private final VerifySubscriptionCodeUseCase verifySubscriptionCodeUseCase;
    private final ReissueSubscriptionCodeUseCase reissueSubscriptionCodeUseCase;

    @Operation(summary = "Lister tous les codes de souscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionCodeResponse>>> listAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Filtre optionnel sur le statut du code")
            @RequestParam(required = false) CodeStatus status) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(
                status == null
                        ? searchSubscriptionCodesUseCase.execute(page, size)
                        : searchSubscriptionCodesUseCase.execute(status, page, size)));
    }

    @Operation(summary = "Détails d'un code de souscription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code trouvé"),
            @ApiResponse(responseCode = "404", description = "Code introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<SubscriptionCodeResponse>> getById(
            @Parameter(description = "UUID du code") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                searchSubscriptionCodesUseCase.executeById(id)));
    }

    @Operation(summary = "Codes générés par une souscription d'achat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/subscription/{subscriptionId}")
    public ResponseEntity<ApiResponseFormat<List<SubscriptionCodeResponse>>> listBySubscription(
            @Parameter(description = "UUID de la souscription d'achat") @PathVariable UUID subscriptionId) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                searchSubscriptionCodesUseCase.executeBySubscription(subscriptionId)));
    }

    @Operation(summary = "Codes d'un acheteur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionCodeResponse>>> listByBuyer(
            @Parameter(description = "UUID de l'acheteur") @PathVariable UUID buyerId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(
                searchSubscriptionCodesUseCase.executeByBuyer(buyerId, page, size)));
    }

    @Operation(summary = "Codes d'une session de concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionCodeResponse>>> listBySession(
            @Parameter(description = "UUID de la session") @PathVariable UUID sessionId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(
                searchSubscriptionCodesUseCase.executeBySession(sessionId, page, size)));
    }

    @Operation(summary = "Vérifier un code",
            description = "Contrôle les conditions d'exploitation du code (achat honoré, session active, "
                    + "cohérence de la consommation) et le répare si nécessaire : un code marqué utilisé "
                    + "sans accès correspondant est remis à disposition avec la même chaîne.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contrôle effectué"),
            @ApiResponse(responseCode = "404", description = "Code introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PostMapping("/{id}/verify")
    public ResponseEntity<ApiResponseFormat<SubscriptionCodeVerificationResponse>> verify(
            @Parameter(description = "UUID du code") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                verifySubscriptionCodeUseCase.execute(id)));
    }

    @Operation(summary = "Invalider et remplacer un code",
            description = "Rend la chaîne définitivement inutilisable et émet un nouveau code pour le même "
                    + "achat. Réservé aux codes non consommés — un code ayant déjà ouvert un accès ne peut "
                    + "pas être remplacé.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code remplacé"),
            @ApiResponse(responseCode = "404", description = "Code introuvable"),
            @ApiResponse(responseCode = "409", description = "Code déjà consommé"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PostMapping("/{id}/reissue")
    public ResponseEntity<ApiResponseFormat<SubscriptionCodeVerificationResponse>> reissue(
            @Parameter(description = "UUID du code") @PathVariable UUID id,
            @Parameter(description = "Motif du remplacement") @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                reissueSubscriptionCodeUseCase.execute(id, reason)));
    }

    @Operation(summary = "Codes d'un concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/concours/{concoursId}")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionCodeResponse>>> listByConcours(
            @Parameter(description = "UUID du concours") @PathVariable UUID concoursId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromPage(
                searchSubscriptionCodesUseCase.executeByConcours(concoursId, page, size)));
    }
}
