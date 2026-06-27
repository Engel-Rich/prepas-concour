package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.response.ConcourSessionResponse;
import com.mutrix.prepa.application.usecases.concours.GetConcourSessionUseCases;
import com.mutrix.prepa.cors.ApiResponseFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/concours-sessions")
@RequiredArgsConstructor
@Tag(name = "Concours Sessions", description = "Gestion des sessions de concours")
public class ConcoursSessionController {

    private final GetConcourSessionUseCases getConcourSessionUseCases;

    @Operation(
            summary = "Récupérer une session par son identifiant",
            description = "Retourne les détails complets d'une session de concours"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session trouvée",
                    content = @Content(schema = @Schema(implementation = ConcourSessionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Session non trouvée"),
            @ApiResponse(responseCode = "400", description = "UUID invalide")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<ConcourSessionResponse>> getById(
            @Parameter(description = "Identifiant UUID de la session",
                    example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable String id
    ) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getConcourSessionUseCases.getById(id)));
    }

    @Operation(
            summary = "Lister les sessions d'un concours",
            description = "Retourne une liste paginée des sessions associées à un concours"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Concours non trouvé")
    })
    @GetMapping("/concours/{concoursId}")
    public ResponseEntity<ApiResponseFormat<Page<ConcourSessionResponse>>> getByConcoursId(
            @Parameter(description = "Identifiant UUID du concours",
                    example = "550e8400-e29b-41d4-a716-446655440000", required = true)
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
