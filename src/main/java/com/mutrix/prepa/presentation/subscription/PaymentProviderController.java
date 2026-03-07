package com.mutrix.prepa.presentation.subscription;
import com.mutrix.prepa.application.dto.commandes.subscription.CreatePaymentProviderCommand;
import com.mutrix.prepa.application.dto.response.subscription.PaymentProviderResponse;
import com.mutrix.prepa.application.usecases.subscriptions.DeletePaymentProviderUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.GetPaymentProviderUseCase;
import com.mutrix.prepa.application.usecases.subscriptions.SavePaymentProviderUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payment-providers")
@RequiredArgsConstructor
@Tag(name = "Fournisseurs de paiement", description = "API de gestion des fournisseurs de paiement")
public class PaymentProviderController {

    private final SavePaymentProviderUseCase savePaymentProviderUseCase;
    private final GetPaymentProviderUseCase getPaymentProviderUseCase;
    private final DeletePaymentProviderUseCase deletePaymentProviderUseCase;

    @Operation(summary = "Créer un fournisseur de paiement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fournisseur créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
    })
    @PostMapping
    public ResponseEntity<ApiResponseFormat<PaymentProviderResponse>> save(
            @RequestBody @Valid CreatePaymentProviderCommand provider) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponseCreate(savePaymentProviderUseCase.execute(provider)));
    }

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

    @Operation(summary = "Supprimer un fournisseur de paiement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Fournisseur supprimé"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deletePaymentProviderUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}