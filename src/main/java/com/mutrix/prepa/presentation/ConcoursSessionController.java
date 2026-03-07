package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.commandes.concours.CreateSessionConcoursDto;
import com.mutrix.prepa.application.dto.commandes.concours.UpdateConcoursSessionDto;
import com.mutrix.prepa.application.dto.response.ConcourSessionResponse;
import com.mutrix.prepa.application.usecases.concours.CreateSessionConcoursUseCase;
import com.mutrix.prepa.application.usecases.concours.GetConcourSessionUseCases;
import com.mutrix.prepa.application.usecases.concours.UpdateConcourSessionUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/concours-sessions")
@RequiredArgsConstructor
@Tag(name = "Concours Sessions", description = "Gestion des sessions de concours")
public class ConcoursSessionController {

    private final CreateSessionConcoursUseCase createSessionConcoursUseCase;
    private final UpdateConcourSessionUseCase updateConcourSessionUseCase;
    private final GetConcourSessionUseCases getConcourSessionUseCases;

    // ============================================================
    // CREATE
    // ============================================================

    @Operation(
            summary = "Créer une nouvelle session de concours",
            description = "Crée une session associée à un concours existant"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Session créée avec succès",
                    content = @Content(schema = @Schema(implementation = ConcourSessionResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Concours non trouvé")
    })
    @PostMapping
    public ResponseEntity<ApiResponseFormat<ConcourSessionResponse>> create(
            @Valid @RequestBody CreateSessionConcoursDto dto
    ) {
        ConcourSessionResponse response = createSessionConcoursUseCase.execute(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseFormat.fromResponse(response));
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Operation(
            summary = "Mettre à jour une session de concours",
            description = "Modifie partiellement ou totalement une session existante"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Session mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = ConcourSessionResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Session non trouvée")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<ConcourSessionResponse>> update(
            @Parameter(
                    description = "Identifiant UUID de la session",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable String id,
            @Valid @RequestBody UpdateConcoursSessionDto dto
    ) {
        UUID uuid = UUID.fromString(id);
        ConcourSessionResponse response = updateConcourSessionUseCase.execute(uuid, dto);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Operation(
            summary = "Récupérer une session par son identifiant",
            description = "Retourne les détails complets d’une session de concours"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Session trouvée",
                    content = @Content(schema = @Schema(implementation = ConcourSessionResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Session non trouvée"),
            @ApiResponse(responseCode = "400", description = "UUID invalide")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<ConcourSessionResponse>> getById(
            @Parameter(
                    description = "Identifiant UUID de la session",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable String id
    ) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getConcourSessionUseCases.getById(id)));
    }

    // ============================================================
    // GET BY CONCOURS ID (Pagination)
    // ============================================================

    @Operation(
            summary = "Lister les sessions d’un concours",
            description = "Retourne une liste paginée des sessions associées à un concours"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Concours non trouvé")
    })
    @GetMapping("/concours/{concoursId}")
    public ResponseEntity<ApiResponseFormat<Page<ConcourSessionResponse>>> getByConcoursId(

            @Parameter(
                    description = "Identifiant UUID du concours",
                    example = "550e8400-e29b-41d4-a716-446655440000",
                    required = true
            )
            @PathVariable String concoursId,

            @Parameter(description = "Numéro de page (commence à 1)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "Nombre d'éléments par page", example = "25")
            @RequestParam(defaultValue = "25") Integer size
    ) {
        return ResponseEntity.ok(
                ApiResponseFormat.fromResponse(
                        getConcourSessionUseCases.getByConcourId(concoursId, page, size)
                )
        );
    }

    // ============================================================
    // LIST ALL (Pagination)
    // ============================================================

    @Operation(
            summary = "Lister toutes les sessions de concours",
            description = "Retourne une liste paginée de toutes les sessions existantes"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<Page<ConcourSessionResponse>>> list(

            @Parameter(description = "Numéro de page (commence à 1)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "Nombre d'éléments par page", example = "25")
            @RequestParam(defaultValue = "25") Integer size
    ) {
        return ResponseEntity.ok(
                ApiResponseFormat.fromResponse(
                        getConcourSessionUseCases.list(page, size)
                )
        );
    }
}
