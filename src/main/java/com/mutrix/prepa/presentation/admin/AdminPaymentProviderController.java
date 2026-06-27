package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.response.subscription.PaymentProviderResponse;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentProviderService;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentProvider;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.infrastructure.services.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/payment-providers")
@RequiredArgsConstructor
@Tag(name = "Admin - Fournisseurs de paiement", description = "CRUD des fournisseurs de paiement (Campay, MundiPay…)")
public class AdminPaymentProviderController {

    private final PaymentProviderService paymentProviderService;
    private final MinioService minioService;

    @Operation(summary = "Lister tous les fournisseurs")
    @GetMapping
    public ResponseEntity<ApiResponseFormat<List<PaymentProviderResponse>>> list() {
        List<PaymentProviderResponse> result = paymentProviderService.list()
                .stream().map(PaymentProviderResponse::fromDomain).toList();
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(result));
    }

    @Operation(summary = "Obtenir un fournisseur par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<PaymentProviderResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponseFormat.fromResponse(PaymentProviderResponse.fromDomain(paymentProviderService.getByID(id))));
    }

    @Operation(summary = "Créer un fournisseur de paiement",
            description = "Accepte multipart/form-data. Le logo est uploadé sur MinIO.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fournisseur créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<PaymentProviderResponse>> create(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {

        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) {
            logoUrl = minioService.uploadFile(logo, "payment-providers");
        }

        PaymentProvider provider = PaymentProvider.builder()
                .name(name)
                .description(description)
                .logoUrl(logoUrl)
                .isActive(true)
                .build();

        PaymentProvider saved = paymentProviderService.save(provider);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponse(PaymentProviderResponse.fromDomain(saved)));
    }

    @Operation(summary = "Mettre à jour un fournisseur",
            description = "Met à jour les champs fournis. Si un nouveau logo est envoyé, il remplace l'ancien sur MinIO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fournisseur mis à jour"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<PaymentProviderResponse>> update(
            @Parameter(description = "UUID du fournisseur") @PathVariable UUID id,
            @RequestParam(value = "name",        required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isActive",    required = false) Boolean isActive,
            @RequestParam(value = "logo",        required = false) MultipartFile logo) {

        PaymentProvider existing = paymentProviderService.getByID(id);

        if (logo != null && !logo.isEmpty()) {
            existing.setLogoUrl(minioService.uploadFile(logo, "payment-providers"));
        }
        if (name        != null) existing.setName(name);
        if (description != null) existing.setDescription(description);
        if (isActive    != null) existing.setIsActive(isActive);

        PaymentProvider saved = paymentProviderService.save(existing);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(PaymentProviderResponse.fromDomain(saved)));
    }

    @Operation(summary = "Supprimer un fournisseur")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Fournisseur supprimé"),
            @ApiResponse(responseCode = "404", description = "Fournisseur introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        paymentProviderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
