package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.commandes.matieres.CreateMatieresDto;
import com.mutrix.prepa.application.dto.commandes.matieres.UpdateMatieresDto;
import com.mutrix.prepa.application.dto.response.MatiereResponseDto;
import com.mutrix.prepa.application.usecases.matieres.CreateMatiereUseCase;
import com.mutrix.prepa.application.usecases.matieres.DeleteMatiereUseCase;
import com.mutrix.prepa.application.usecases.matieres.GetMatiereUseCases;
import com.mutrix.prepa.application.usecases.matieres.UpdateMatiereUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.models.Matieres;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/matieres")
@RequiredArgsConstructor
@Tag(name = "Gestion des matières", description = "API permettant la gestion complète des matières (création, modification, suppression, consultation)")
public class MatieresController {

        private final CreateMatiereUseCase createMatiereUseCase;
        private final UpdateMatiereUseCase updateMatiereUseCase;
        private final GetMatiereUseCases getMatiereUseCases;
        private final DeleteMatiereUseCase deleteMatiereUseCase;

        // <==============================>//

        @Operation(summary = "Lister les matières", description = "Retourne une liste paginée des matières disponibles")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès", content = @Content(schema = @Schema(implementation = PageResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Paramètres de pagination invalides"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne serveur")
        })
        @GetMapping
        public ResponseEntity<ApiResponseFormat<PageResponse<MatiereResponseDto>>> getMatieres(
                        @Parameter(description = "Numéro de page (commence à 1)", example = "1") @RequestParam(defaultValue = "1") Integer page,

                        @Parameter(description = "Nombre d'éléments par page", example = "20") @RequestParam(defaultValue = "25") Integer size) {
                Page<Matieres> matieresPage = getMatiereUseCases.getMatieres(page, size);

                PageResponse<MatiereResponseDto> response = PageResponse.<MatiereResponseDto>builder()
                                .content(matieresPage.getContent().stream()
                                                .map(this::mapResponseFromModel)
                                                .toList())
                                .page(matieresPage.getNumber())
                                .size(matieresPage.getSize())
                                .totalElements(matieresPage.getTotalElements())
                                .totalPages(matieresPage.getTotalPages())
                                .last(matieresPage.isLast())
                                .build();

                return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
        }

        // <==============================>//

        @Operation(summary = "Récupérer une matière par ID", description = "Retourne les informations détaillées d'une matière spécifique")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Matière trouvée", content = @Content(schema = @Schema(implementation = MatiereResponseDto.class))),
                        @ApiResponse(responseCode = "404", description = "Matière non trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne serveur")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ApiResponseFormat<MatiereResponseDto>> getMatiereById(
                        @Parameter(description = "Identifiant UUID de la matière") @PathVariable UUID id) {
                Matieres matieres = getMatiereUseCases.getMatiereById(id);

                return ResponseEntity.ok(ApiResponseFormat.fromResponse( mapResponseFromModel(matieres)));
        }

        // <==============================>//

        @Operation(summary = "Créer une matière", description = "Permet de créer une nouvelle matière dans le système")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Matière créée avec succès", content = @Content(schema = @Schema(implementation = MatiereResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "409", description = "Conflit (matière déjà existante)"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne serveur")
        })
        @PostMapping
        public ResponseEntity<ApiResponseFormat<MatiereResponseDto>> createMatiere(
                        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Données nécessaires à la création d'une matière", required = true) @RequestBody @Valid CreateMatieresDto dto) {
                Matieres matieres = createMatiereUseCase.execute(dto);

                return ResponseEntity.status(201)
                                .body(ApiResponseFormat.fromResponse( mapResponseFromModel(matieres)));
        }

        // <==============================>//

        @Operation(summary = "Mettre à jour une matière", description = "Met à jour les informations d'une matière existante")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Matière mise à jour avec succès", content = @Content(schema = @Schema(implementation = MatiereResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "404", description = "Matière non trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne serveur")
        })
        @PutMapping("/{id}")
        public ResponseEntity<ApiResponseFormat<MatiereResponseDto>> updateMatiere(
                        @Parameter(description = "Identifiant UUID de la matière") @PathVariable UUID id,

                        @RequestBody @Valid UpdateMatieresDto updateMatieresDto) {
                Matieres matieres = updateMatiereUseCase.execute(id, updateMatieresDto);

                return ResponseEntity.ok(ApiResponseFormat.fromResponse( mapResponseFromModel(matieres)));
        }

        // <==============================>//

        @Operation(summary = "Supprimer une matière", description = "Supprime définitivement une matière par son identifiant")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Matière supprimée avec succès"),
                        @ApiResponse(responseCode = "404", description = "Matière non trouvée"),
                        @ApiResponse(responseCode = "500", description = "Erreur interne serveur")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteMatiere(
                        @Parameter(description = "Identifiant UUID de la matière") @PathVariable UUID id) {
                deleteMatiereUseCase.execute(id);

                return ResponseEntity.noContent().build();
        }

        // <==============================>//

        private MatiereResponseDto mapResponseFromModel(Matieres matieres) {
                return MatiereResponseDto.builder()
                                .id(matieres.getId())
                                .name(matieres.getName())
                                .description(matieres.getDescription())
                                .logoUrl(matieres.getLogoUrl())
                                .createdAt(matieres.getCreatedAt())
                                .updatedAt(matieres.getUpdatedAt())
                                .isActive(matieres.getIsActive())
                                .metadata(matieres.getMetadata())
                                .build();
        }
}
