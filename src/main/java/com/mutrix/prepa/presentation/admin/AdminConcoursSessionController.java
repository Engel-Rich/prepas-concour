package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.commandes.concours.CreateSessionConcoursDto;
import com.mutrix.prepa.application.dto.commandes.concours.UpdateConcoursSessionDto;
import com.mutrix.prepa.application.dto.response.ConcourSessionResponse;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.application.dto.response.subscription.TransactionResponse;
import com.mutrix.prepa.application.usecases.concours.CreateSessionConcoursUseCase;
import com.mutrix.prepa.application.usecases.concours.GetConcourSessionUseCases;
import com.mutrix.prepa.application.usecases.concours.UpdateConcourSessionUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/concours-sessions")
@RequiredArgsConstructor
@Tag(name = "Admin - Sessions de concours", description = "Gestion des sessions de concours par l'administrateur")
public class AdminConcoursSessionController {

    private final CreateSessionConcoursUseCase createSessionConcoursUseCase;
    private final UpdateConcourSessionUseCase updateConcourSessionUseCase;
    private final GetConcourSessionUseCases getConcourSessionUseCases;
    private final SubscriptionServices subscriptionServices;
    private final TransactionServices transactionServices;

    @Operation(summary = "Créer une session de concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Session créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PostMapping
    public ResponseEntity<ApiResponseFormat<ConcourSessionResponse>> create(
            @Valid @RequestBody CreateSessionConcoursDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponse(createSessionConcoursUseCase.execute(dto)));
    }

    @Operation(summary = "Mettre à jour une session de concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session mise à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Session introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<ConcourSessionResponse>> update(
            @Parameter(description = "UUID de la session") @PathVariable String id,
            @Valid @RequestBody UpdateConcoursSessionDto dto) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                updateConcourSessionUseCase.execute(UUID.fromString(id), dto)));
    }

    @Operation(summary = "Récupérer une session par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session trouvée"),
            @ApiResponse(responseCode = "404", description = "Session introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<ConcourSessionResponse>> getById(
            @Parameter(description = "UUID de la session") @PathVariable String id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getConcourSessionUseCases.getById(id)));
    }

    @Operation(summary = "Lister les sessions d'un concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/concours/{concoursId}")
    public ResponseEntity<ApiResponseFormat<Page<ConcourSessionResponse>>> getByConcoursId(
            @Parameter(description = "UUID du concours") @PathVariable String concoursId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "25") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                getConcourSessionUseCases.getByConcourId(concoursId, page, size)));
    }

    @Operation(summary = "Lister toutes les sessions de concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<Page<ConcourSessionResponse>>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "25") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getConcourSessionUseCases.list(page, size)));
    }

    // ── Subscriptions d'une session ────────────────────────────────────────────

    @Operation(summary = "Lister les abonnements d'une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{sessionId}/subscriptions")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> getSubscriptions(
            @Parameter(description = "UUID de la session") @PathVariable UUID sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = subscriptionServices.searchBySession(sessionId, page, size)
                .map(SubscriptionResponse::fromDomain);
        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }

    // ── Transactions d'une session ─────────────────────────────────────────────

    @Operation(summary = "Lister les transactions d'une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{sessionId}/transactions")
    public ResponseEntity<ApiResponseFormat<PageResponse<TransactionResponse>>> getTransactions(
            @Parameter(description = "UUID de la session") @PathVariable UUID sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = transactionServices.listBySession(sessionId, page, size)
                .map(TransactionResponse::fromDomain);
        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }
}
