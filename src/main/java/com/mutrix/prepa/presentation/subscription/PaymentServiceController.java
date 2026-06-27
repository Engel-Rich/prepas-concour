package com.mutrix.prepa.presentation.subscription;

import com.mutrix.prepa.application.dto.response.subscription.PaymentServiceResponse;
import com.mutrix.prepa.application.usecases.subscriptions.GetPaymentServiceUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payment-services")
@RequiredArgsConstructor
@Tag(name = "Services de paiement", description = "API de consultation des services de paiement")
public class PaymentServiceController {

    private final GetPaymentServiceUseCase getPaymentServiceUseCase;

    @Operation(summary = "Obtenir un service par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service trouvé",
                    content = @Content(schema = @Schema(implementation = ApiResponseFormat.class))),
            @ApiResponse(responseCode = "404", description = "Service introuvable",
                    content = @Content(schema = @Schema(implementation = ApiResponseFormat.class))),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<PaymentServiceResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getPaymentServiceUseCase.executeById(id)));
    }

    @Operation(summary = "Lister les services par fournisseur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "404", description = "provider id not found",
                    content = @Content(schema = @Schema(implementation = ApiResponseFormat.class)))
    })
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<ApiResponseFormat<List<PaymentServiceResponse>>> getByProviderId(
            @PathVariable UUID providerId) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getPaymentServiceUseCase.executeByProviderId(providerId)));
    }

    @Operation(summary = "Lister les services par sens de transaction", description = "Filtre les services par DEBIT ou CREDIT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "400", description = "Sens must be IN or OUT",
                    content = @Content(schema = @Schema(implementation = ApiResponseFormat.class)))
    })
    @GetMapping("/sens/{sens}")
    public ResponseEntity<ApiResponseFormat<List<PaymentServiceResponse>>> getBySens(
            @PathVariable TransactionSens sens) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getPaymentServiceUseCase.executeBySens(sens)));
    }

    @Operation(summary = "Lister tous les services de paiement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<List<PaymentServiceResponse>>> list() {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getPaymentServiceUseCase.executeList()));
    }
}
