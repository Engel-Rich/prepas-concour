package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.response.subscription.PaymentServiceResponse;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.domaines.interfaces.subscriptions.PaymentServiceService;
import com.mutrix.prepa.domaines.models.subscriptions.PaymentService;
import com.mutrix.prepa.domaines.valueobjects.TransactionSens;
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
@RequestMapping("/admin/payment-services")
@RequiredArgsConstructor
@Tag(name = "Admin - Services de paiement", description = "CRUD des services de paiement (OM Dépôt, MoMo Dépôt…)")
public class AdminPaymentServiceController {

    private final PaymentServiceService paymentServiceService;
    private final MinioService minioService;

    @Operation(summary = "Lister tous les services")
    @GetMapping
    public ResponseEntity<ApiResponseFormat<List<PaymentServiceResponse>>> list() {
        List<PaymentServiceResponse> result = paymentServiceService.list()
                .stream().map(PaymentServiceResponse::fromDomain).toList();
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(result));
    }

    @Operation(summary = "Obtenir un service par ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<PaymentServiceResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponseFormat.fromResponse(PaymentServiceResponse.fromDomain(paymentServiceService.getById(id))));
    }

    @Operation(summary = "Créer un service de paiement",
            description = "Accepte multipart/form-data. Le logo est uploadé sur MinIO.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Service créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<PaymentServiceResponse>> create(
            @RequestParam("name")              String name,
            @RequestParam("paymentProviderId") UUID paymentProviderId,
            @RequestParam("sens")              TransactionSens sens,
            @RequestParam("providerRate")      Double providerRate,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "rate",        required = false) Double rate,
            @RequestParam(value = "regExp",      required = false) String regExp,
            @RequestParam(value = "logo",        required = false) MultipartFile logo) {

        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) {
            logoUrl = minioService.uploadFile(logo, "payment-services");
        }

        PaymentService service = PaymentService.builder()
                .name(name)
                .description(description)
                .logoUrl(logoUrl)
                .paymentProviderId(paymentProviderId)
                .sens(sens)
                .providerRate(providerRate)
                .rate(rate)
                .regExp(regExp)
                .isActive(true)
                .build();

        PaymentService saved = paymentServiceService.save(service);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponse(PaymentServiceResponse.fromDomain(saved)));
    }

    @Operation(summary = "Mettre à jour un service de paiement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Service mis à jour"),
            @ApiResponse(responseCode = "404", description = "Service introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<PaymentServiceResponse>> update(
            @Parameter(description = "UUID du service") @PathVariable UUID id,
            @RequestParam(value = "name",              required = false) String name,
            @RequestParam(value = "description",       required = false) String description,
            @RequestParam(value = "paymentProviderId", required = false) UUID paymentProviderId,
            @RequestParam(value = "sens",              required = false) TransactionSens sens,
            @RequestParam(value = "providerRate",      required = false) Double providerRate,
            @RequestParam(value = "rate",              required = false) Double rate,
            @RequestParam(value = "regExp",            required = false) String regExp,
            @RequestParam(value = "isActive",          required = false) Boolean isActive,
            @RequestParam(value = "logo",              required = false) MultipartFile logo) {

        PaymentService existing = paymentServiceService.getById(id);

        if (logo             != null && !logo.isEmpty()) existing.setLogoUrl(minioService.uploadFile(logo, "payment-services"));
        if (name             != null) existing.setName(name);
        if (description      != null) existing.setDescription(description);
        if (paymentProviderId!= null) existing.setPaymentProviderId(paymentProviderId);
        if (sens             != null) existing.setSens(sens);
        if (providerRate     != null) existing.setProviderRate(providerRate);
        if (rate             != null) existing.setRate(rate);
        if (regExp           != null) existing.setRegExp(regExp);
        if (isActive         != null) existing.setIsActive(isActive);

        PaymentService saved = paymentServiceService.save(existing);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(PaymentServiceResponse.fromDomain(saved)));
    }

    @Operation(summary = "Supprimer un service de paiement")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Service supprimé"),
            @ApiResponse(responseCode = "404", description = "Service introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        paymentServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
