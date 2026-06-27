package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.application.dto.response.MatiereResponseDto;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.infrastructure.services.SessionAssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/concours-sessions")
@RequiredArgsConstructor
@Tag(name = "Admin - Associations Session", description = "Gestion des matières et cours associés aux sessions de concours")
public class AdminSessionAssociationsController {

    private final SessionAssociationService sessionAssociationService;

    // ═══════════════════════════════════════════════════════════════
    //  MATIÈRES
    // ═══════════════════════════════════════════════════════════════

    @Operation(summary = "Lister les matières d'une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "404", description = "Session introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{sessionId}/matieres")
    public ResponseEntity<ApiResponseFormat<List<MatiereResponseDto>>> getMatieresForSession(
            @Parameter(description = "UUID de la session") @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                sessionAssociationService.getMatieresForSession(sessionId)));
    }

    @Operation(summary = "Associer une matière à une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Association créée"),
            @ApiResponse(responseCode = "404", description = "Session ou matière introuvable"),
            @ApiResponse(responseCode = "409", description = "Association déjà existante"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PostMapping("/{sessionId}/matieres/{matiereId}")
    public ResponseEntity<ApiResponseFormat<MatiereResponseDto>> addMatiereToSession(
            @Parameter(description = "UUID de la session")  @PathVariable UUID sessionId,
            @Parameter(description = "UUID de la matière") @PathVariable UUID matiereId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponse(
                        sessionAssociationService.addMatiereToSession(sessionId, matiereId)));
    }

    @Operation(summary = "Retirer une matière d'une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Association supprimée"),
            @ApiResponse(responseCode = "404", description = "Association introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @DeleteMapping("/{sessionId}/matieres/{matiereId}")
    public ResponseEntity<Void> removeMatiereFromSession(
            @Parameter(description = "UUID de la session")  @PathVariable UUID sessionId,
            @Parameter(description = "UUID de la matière") @PathVariable UUID matiereId) {
        sessionAssociationService.removeMatiereFromSession(sessionId, matiereId);
        return ResponseEntity.noContent().build();
    }

    // ═══════════════════════════════════════════════════════════════
    //  COURS
    // ═══════════════════════════════════════════════════════════════

    @Operation(summary = "Lister les cours d'une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "404", description = "Session introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{sessionId}/cours")
    public ResponseEntity<ApiResponseFormat<List<CoursResponse>>> getCoursForSession(
            @Parameter(description = "UUID de la session") @PathVariable UUID sessionId) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                sessionAssociationService.getCoursForSession(sessionId)));
    }

    @Operation(summary = "Associer un cours à une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Association créée"),
            @ApiResponse(responseCode = "404", description = "Session ou cours introuvable"),
            @ApiResponse(responseCode = "409", description = "Association déjà existante"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PostMapping("/{sessionId}/cours/{coursId}")
    public ResponseEntity<ApiResponseFormat<CoursResponse>> addCoursToSession(
            @Parameter(description = "UUID de la session") @PathVariable UUID sessionId,
            @Parameter(description = "UUID du cours")     @PathVariable UUID coursId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponse(
                        sessionAssociationService.addCoursToSession(sessionId, coursId)));
    }

    @Operation(summary = "Retirer un cours d'une session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Association supprimée"),
            @ApiResponse(responseCode = "404", description = "Association introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @DeleteMapping("/{sessionId}/cours/{coursId}")
    public ResponseEntity<Void> removeCoursFromSession(
            @Parameter(description = "UUID de la session") @PathVariable UUID sessionId,
            @Parameter(description = "UUID du cours")     @PathVariable UUID coursId) {
        sessionAssociationService.removeCoursFromSession(sessionId, coursId);
        return ResponseEntity.noContent().build();
    }
}
