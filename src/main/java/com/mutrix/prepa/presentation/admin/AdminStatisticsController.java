package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.response.admin.ConcoursStatisticsResponse;
import com.mutrix.prepa.application.dto.response.admin.GlobalStatisticsResponse;
import com.mutrix.prepa.application.dto.response.admin.SubscriptionStatisticsResponse;
import com.mutrix.prepa.application.dto.response.admin.UserStatisticsResponse;
import com.mutrix.prepa.application.usecases.admin.GetConcoursStatisticsUseCase;
import com.mutrix.prepa.application.usecases.admin.GetGlobalStatisticsUseCase;
import com.mutrix.prepa.application.usecases.admin.GetSubscriptionStatisticsUseCase;
import com.mutrix.prepa.application.usecases.admin.GetUserStatisticsUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
@Tag(name = "Admin - Statistiques", description = "Tableau de bord et statistiques globales")
public class AdminStatisticsController {

    private final GetGlobalStatisticsUseCase getGlobalStatisticsUseCase;
    private final GetUserStatisticsUseCase getUserStatisticsUseCase;
    private final GetConcoursStatisticsUseCase getConcoursStatisticsUseCase;
    private final GetSubscriptionStatisticsUseCase getSubscriptionStatisticsUseCase;

    @Operation(summary = "Statistiques globales", description = "Retourne un résumé de toutes les statistiques de la plateforme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<GlobalStatisticsResponse>> getGlobal() {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getGlobalStatisticsUseCase.execute()));
    }

    @Operation(summary = "Statistiques des utilisateurs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/users")
    public ResponseEntity<ApiResponseFormat<UserStatisticsResponse>> getUsers() {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getUserStatisticsUseCase.execute()));
    }

    @Operation(summary = "Statistiques des concours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/concours")
    public ResponseEntity<ApiResponseFormat<ConcoursStatisticsResponse>> getConcours() {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getConcoursStatisticsUseCase.execute()));
    }

    @Operation(summary = "Statistiques des inscriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistiques récupérées"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/subscriptions")
    public ResponseEntity<ApiResponseFormat<SubscriptionStatisticsResponse>> getSubscriptions() {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getSubscriptionStatisticsUseCase.execute()));
    }
}
