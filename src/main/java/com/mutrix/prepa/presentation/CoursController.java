package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.commandes.cours.CreateCoursCommand;
import com.mutrix.prepa.application.dto.commandes.cours.UpdateCoursCommand;
import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.application.usecases.cours.*;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cours")
@RequiredArgsConstructor
@Tag(name = "Cours", description = "API de gestion des cours")
public class CoursController {

    private final CreateCoursUseCase createCoursUseCase;
    private final UpdateCoursUseCase updateCoursUseCase;
    private final GetCoursUseCase getCoursUseCase;
    private final GetAllCoursUseCase getAllCoursUseCase;
    private final GetCoursByMatiereUseCase getCoursByMatiereUseCase;
    private final DeleteCoursUseCase deleteCoursUseCase;

    // Récupère l'utilisateur connecté depuis le contexte de sécurité
    private UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Adapter selon votre implémentation de UserDetails
        return ((SecurityUser) auth.getPrincipal()).getUser().getId();
    }

    @Operation(summary = "Créer un cours", description = "Crée un nouveau cours pour l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cours créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
    })
    @PostMapping
    public ResponseEntity<ApiResponseFormat<CoursResponse>> createCours(
            @RequestBody @Valid CreateCoursCommand command) {
        UUID userId = getAuthenticatedUserId();
        CoursResponse response = createCoursUseCase.execute(command, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponse(response));
    }

    @Operation(summary = "Mettre à jour un cours", description = "Met à jour un cours existant appartenant à l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<CoursResponse>> updateCours(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateCoursCommand command) {
        UUID userId = getAuthenticatedUserId();
        CoursResponse response = updateCoursUseCase.execute(id, command, userId);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    @Operation(summary = "Obtenir un cours par ID", description = "Retourne les détails d'un cours spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours trouvé"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<CoursResponse>> getCoursById(
            @PathVariable UUID id) {
        CoursResponse response = getCoursUseCase.execute(id);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    @Operation(summary = "Lister tous les cours", description = "Retourne la liste paginée de tous les cours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "400", description = "Paramètres de pagination invalides"),
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<PageResponse<CoursResponse>>> getAllCours(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResponse<CoursResponse> response = getAllCoursUseCase.execute(page, size);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    @Operation(summary = "Lister les cours par matière", description = "Retourne la liste paginée des cours filtrés par matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Matière introuvable"),
    })
    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<ApiResponseFormat<PageResponse<CoursResponse>>> getCoursByMatiere(
            @PathVariable UUID matiereId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<CoursResponse> response = getCoursByMatiereUseCase.execute(matiereId, pageable);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    @Operation(summary = "Supprimer un cours", description = "Supprime un cours appartenant à l'utilisateur connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cours supprimé avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCours(@PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        deleteCoursUseCase.execute(id, userId);
        return ResponseEntity.noContent().build();
    }
}