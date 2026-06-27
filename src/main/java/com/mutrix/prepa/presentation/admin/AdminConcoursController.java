package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.response.ConcourResponseDTO;
import com.mutrix.prepa.application.dto.response.subscription.SubscriptionResponse;
import com.mutrix.prepa.application.dto.response.subscription.TransactionResponse;
import com.mutrix.prepa.application.usecases.concours.CreateConcoursUseCase;
import com.mutrix.prepa.application.usecases.concours.GetConcoursUseCases;
import com.mutrix.prepa.application.usecases.concours.UpdateConcoursUseCease;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.SubscriptionServices;
import com.mutrix.prepa.domaines.interfaces.subscriptions.TransactionServices;
import com.mutrix.prepa.infrastructure.services.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/admin/concours")
@RequiredArgsConstructor
@Tag(name = "Admin - Concours", description = "Gestion des concours par l'administrateur")
public class AdminConcoursController {

    private final CreateConcoursUseCase createConcoursUseCase;
    private final UpdateConcoursUseCease updateConcoursUseCease;
    private final GetConcoursUseCases getConcoursUseCases;
    private final MinioService minioService;
    private final SubscriptionServices subscriptionServices;
    private final TransactionServices transactionServices;

    @Operation(summary = "Créer un concours (multipart/form-data)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Concours créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<ConcourResponseDTO>> create(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isActive", required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {

        String logoUrl = (logo != null && !logo.isEmpty()) ? minioService.uploadFile(logo, "concours") : null;
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponse(
                        createConcoursUseCase.execute(name, description, isActive, logoUrl)));
    }

    @Operation(summary = "Mettre à jour un concours (multipart/form-data)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Concours mis à jour"),
            @ApiResponse(responseCode = "404", description = "Concours introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<ConcourResponseDTO>> update(
            @Parameter(description = "UUID du concours") @PathVariable String id,
            @RequestParam(value = "name",        required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isActive",    required = false) Boolean isActive,
            @RequestParam(value = "logo",        required = false) MultipartFile logo) {

        String logoUrl = (logo != null && !logo.isEmpty()) ? minioService.uploadFile(logo, "concours") : null;
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponseFormat.fromResponse(
                        updateConcoursUseCease.execute(UUID.fromString(id), name, description, isActive, logoUrl)));
    }

    @Operation(summary = "Récupérer un concours par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Concours trouvé"),
            @ApiResponse(responseCode = "404", description = "Concours introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<ConcourResponseDTO>> getById(
            @Parameter(description = "UUID du concours") @PathVariable String id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getConcoursUseCases.getById(id)));
    }

    @Operation(summary = "Lister tous les concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<Page<ConcourResponseDTO>>> list(
            @Parameter(description = "Numéro de page (commence à 1)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "Nombre d'éléments par page", example = "20")
            @RequestParam(defaultValue = "20") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                getConcoursUseCases.list(page == null ? 1 : page, size == 0 ? 25 : size)));
    }

    // ── Subscriptions d'un concours ────────────────────────────────────────────

    @Operation(summary = "Lister les abonnements d'un concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{concoursId}/subscriptions")
    public ResponseEntity<ApiResponseFormat<PageResponse<SubscriptionResponse>>> getSubscriptions(
            @Parameter(description = "UUID du concours") @PathVariable UUID concoursId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = subscriptionServices.searchByConcours(concoursId, page, size)
                .map(SubscriptionResponse::fromDomain);
        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }

    // ── Transactions d'un concours ─────────────────────────────────────────────

    @Operation(summary = "Lister les transactions d'un concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{concoursId}/transactions")
    public ResponseEntity<ApiResponseFormat<PageResponse<TransactionResponse>>> getTransactions(
            @Parameter(description = "UUID du concours") @PathVariable UUID concoursId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var result = transactionServices.listByConcours(concoursId, page, size)
                .map(TransactionResponse::fromDomain);
        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }
}
