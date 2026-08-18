package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.response.ConcourSessionResponse;
import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.application.usecases.concours.GetConcourSessionUseCases;
import com.mutrix.prepa.application.usecases.cours.GetSessionCoursListUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/concours-sessions")
@RequiredArgsConstructor
@Tag(name = "Concours Sessions", description = "Gestion des sessions de concours")
public class ConcoursSessionController {

    private final GetConcourSessionUseCases getConcourSessionUseCases;
    private final GetSessionCoursListUseCase getSessionCoursListUseCase;

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

    @Operation(
            summary = "Lister les cours d'une session",
            description = "Retourne les cours associés à cette session. Le videoUrl est inclus uniquement pour les utilisateurs ayant une souscription RUNNING ou pour les cours gratuits."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Session non trouvée")
    })
    @GetMapping("/{sessionId}/cours")
    public ResponseEntity<ApiResponseFormat<List<CoursResponse>>> getSessionCours(
            @Parameter(description = "Identifiant UUID de la session", required = true)
            @PathVariable UUID sessionId,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        UUID userId = securityUser != null ? securityUser.getUser().getId() : null;
        List<CoursResponse> response = getSessionCoursListUseCase.execute(sessionId, userId);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    @Operation(
            summary = "Lister les cours d'une matière pour une session",
            description = "Retourne uniquement les cours associés à la session et appartenant à la matière demandée. Le videoUrl est inclus pour les cours gratuits ou si l'utilisateur possède une souscription RUNNING à cette session."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours récupérés avec succès, ou liste vide si aucun cours ne correspond"),
            @ApiResponse(responseCode = "400", description = "UUID invalide")
    })
    @GetMapping("/{sessionId}/matieres/{matiereId}/cours")
    public ResponseEntity<ApiResponseFormat<List<CoursResponse>>> getSessionCoursByMatiere(
            @Parameter(description = "Identifiant UUID de la session", required = true)
            @PathVariable UUID sessionId,
            @Parameter(description = "Identifiant UUID de la matière", required = true)
            @PathVariable UUID matiereId,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        UUID userId = securityUser != null ? securityUser.getUser().getId() : null;
        List<CoursResponse> response = getSessionCoursListUseCase.execute(sessionId, matiereId, userId);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }
}
