package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.response.subscription.TransactionResponse;
import com.mutrix.prepa.application.usecases.subscriptions.DeleteTransactionUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.GetTransactionByIdUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.ListTransactionsUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/transactions")
@RequiredArgsConstructor
@Tag(name = "Admin - Transactions", description = "Consultation et gestion des transactions de paiement")
public class AdminTransactionsController {

    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetTransactionByIdUseCase getTransactionByIdUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    @Operation(summary = "Lister toutes les transactions", description = "Retourne la liste paginée de toutes les transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<PageResponse<TransactionResponse>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TransactionResponse> result = listTransactionsUseCase.execute(page, size);
        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }

    @Operation(summary = "Obtenir une transaction par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction trouvée"),
            @ApiResponse(responseCode = "404", description = "Transaction introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<TransactionResponse>> getById(
            @Parameter(description = "UUID de la transaction") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getTransactionByIdUseCase.execute(id)));
    }

    @Operation(summary = "Supprimer une transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction supprimée"),
            @ApiResponse(responseCode = "404", description = "Transaction introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID de la transaction") @PathVariable UUID id) {
        deleteTransactionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
