package com.mutrix.prepa.presentation.subscription;

import com.mutrix.prepa.application.dto.response.subscription.TransactionResponse;
import com.mutrix.prepa.application.usecases.subscriptions.DeleteTransactionUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.GetTransactionByIdUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.ListTransactionsUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "API de gestion des transactions")
public class TransactionController {

    private final GetTransactionByIdUseCase getTransactionByIdUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    private UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) auth.getPrincipal();
        return securityUser.getUser().getId();
    }

    @Operation(summary = "Obtenir une transaction par ID", description = "Retourne les détails d'une transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction trouvée"),
            @ApiResponse(responseCode = "404", description = "Transaction introuvable"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<TransactionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getTransactionByIdUseCase.execute(id)));
    }

    @Operation(summary = "Lister mes transactions", description = "Retourne la liste paginée des transactions de l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
    })
    @GetMapping("/me")
    public ResponseEntity<ApiResponseFormat<PageResponse<TransactionResponse>>> listMyTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID userId = getAuthenticatedUserId();
        Page<TransactionResponse> result = listTransactionsUseCase.execute(userId, page, size);
        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }

    @Operation(summary = "Lister toutes les transactions", description = "Retourne la liste paginée de toutes les transactions (admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<PageResponse<TransactionResponse>>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionResponse> result = listTransactionsUseCase.execute(page, size);
        return ResponseEntity.ok(ApiResponseFormat.fromPage(result));
    }

    @Operation(summary = "Supprimer une transaction", description = "Supprime une transaction par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction supprimée"),
            @ApiResponse(responseCode = "404", description = "Transaction introuvable"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteTransactionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}