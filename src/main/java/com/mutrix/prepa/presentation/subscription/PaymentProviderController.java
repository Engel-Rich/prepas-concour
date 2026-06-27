package com.mutrix.prepa.presentation.subscription;

import com.mutrix.prepa.application.dto.response.subscription.PaymentProviderResponse;
import com.mutrix.prepa.application.usecases.subscriptions.GetPaymentProviderUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payment-providers")
@RequiredArgsConstructor
@Tag(name = "Fournisseurs de paiement", description = "API de consultation des fournisseurs de paiement")
public class PaymentProviderController {

    private final GetPaymentProviderUseCase getPaymentProviderUseCase;

    @Operation(summary = "Obtenir un fournisseur par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fournisseur trouvé"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<PaymentProviderResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getPaymentProviderUseCase.executeById(id)));
    }

    @Operation(summary = "Lister tous les fournisseurs de paiement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<List<PaymentProviderResponse>>> list() {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getPaymentProviderUseCase.executeList()));
    }
}
