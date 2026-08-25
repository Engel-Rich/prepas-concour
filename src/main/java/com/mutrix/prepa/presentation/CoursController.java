package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.application.dto.response.cours.VideoKeyResponse;
import com.mutrix.prepa.application.dto.response.cours.VideoMetadataResponse;
import com.mutrix.prepa.application.usecases.cours.GetAllCoursUseCase;
import com.mutrix.prepa.application.usecases.cours.GetCoursByMatiereUseCase;
import com.mutrix.prepa.application.usecases.cours.GetCoursUseCase;
import com.mutrix.prepa.application.usecases.cours.GetCoursVideoKeyUseCase;
import com.mutrix.prepa.application.usecases.cours.GetCoursVideoMetadataUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cours")
@RequiredArgsConstructor
@Tag(name = "Cours", description = "API de gestion des cours")
public class CoursController {

    private final GetCoursUseCase getCoursUseCase;
    private final GetAllCoursUseCase getAllCoursUseCase;
    private final GetCoursByMatiereUseCase getCoursByMatiereUseCase;
    private final GetCoursVideoKeyUseCase getCoursVideoKeyUseCase;
    private final GetCoursVideoMetadataUseCase getCoursVideoMetadataUseCase;

    @Operation(summary = "Obtenir un cours par ID", description = "Retourne les détails d'un cours spécifique. Le videoUrl est masqué si l'utilisateur n'a pas de souscription active.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours trouvé"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<CoursResponse>> getCoursById(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        UUID userId = securityUser != null ? securityUser.getUser().getId() : null;
        CoursResponse response = getCoursUseCase.execute(id, userId);
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

    @Operation(summary = "Obtenir la clé de déchiffrement d'une vidéo",
            description = "Retourne la clé de contenu permettant de lire une vidéo chiffrée. "
                    + "Réservée aux comptes disposant d'un accès actif au cours, ou aux cours gratuits. "
                    + "Le client conserve la clé dans son coffre sécurisé pour la lecture hors connexion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clé délivrée"),
            @ApiResponse(responseCode = "401", description = "Accès refusé — aucune souscription active"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable ou vidéo non chiffrée"),
    })
    @GetMapping("/{id}/video-key")
    public ResponseEntity<ApiResponseFormat<VideoKeyResponse>> getVideoKey(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        UUID userId = securityUser != null ? securityUser.getUser().getId() : null;
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                getCoursVideoKeyUseCase.execute(id, userId)));
    }

    @Operation(summary = "Métadonnées de la vidéo d'un cours",
            description = "Taille, empreinte et support des requêtes Range. Le client compare "
                    + "l'empreinte à celle mémorisée lors d'un téléchargement partiel : si elle "
                    + "a changé, le fragment local est obsolète et doit être rejeté.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Métadonnées récupérées"),
            @ApiResponse(responseCode = "401", description = "Accès refusé"),
            @ApiResponse(responseCode = "404", description = "Cours ou vidéo introuvable"),
    })
    @GetMapping("/{id}/video-metadata")
    public ResponseEntity<ApiResponseFormat<VideoMetadataResponse>> getVideoMetadata(
            @PathVariable UUID id,
            @AuthenticationPrincipal SecurityUser securityUser) {
        UUID userId = securityUser != null ? securityUser.getUser().getId() : null;
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                getCoursVideoMetadataUseCase.execute(id, userId)));
    }
}
