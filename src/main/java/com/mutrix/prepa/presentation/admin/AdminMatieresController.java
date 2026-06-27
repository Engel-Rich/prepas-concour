package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.response.MatiereResponseDto;
import com.mutrix.prepa.application.usecases.matieres.CreateMatiereUseCase;
import com.mutrix.prepa.application.usecases.matieres.DeleteMatiereUseCase;
import com.mutrix.prepa.application.usecases.matieres.GetMatiereUseCases;
import com.mutrix.prepa.application.usecases.matieres.UpdateMatiereUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.domaines.models.Matieres;
import com.mutrix.prepa.infrastructure.services.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/admin/matieres")
@RequiredArgsConstructor
@Tag(name = "Admin - Matières", description = "Gestion des matières par l'administrateur")
public class AdminMatieresController {

    private final CreateMatiereUseCase createMatiereUseCase;
    private final UpdateMatiereUseCase updateMatiereUseCase;
    private final GetMatiereUseCases getMatiereUseCases;
    private final DeleteMatiereUseCase deleteMatiereUseCase;
    private final MinioService minioService;

    @Operation(summary = "Lister les matières")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<PageResponse<MatiereResponseDto>>> getMatieres(
            @Parameter(description = "Numéro de page (commence à 1)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Nombre d'éléments par page", example = "25")
            @RequestParam(defaultValue = "25") Integer size) {
        Page<Matieres> matieresPage = getMatiereUseCases.getMatieres(page, size);
        PageResponse<MatiereResponseDto> response = PageResponse.<MatiereResponseDto>builder()
                .content(matieresPage.getContent().stream().map(this::toDto).toList())
                .page(matieresPage.getNumber())
                .size(matieresPage.getSize())
                .totalElements(matieresPage.getTotalElements())
                .totalPages(matieresPage.getTotalPages())
                .last(matieresPage.isLast())
                .build();
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(response));
    }

    @Operation(summary = "Récupérer une matière par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière trouvée"),
            @ApiResponse(responseCode = "404", description = "Matière introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<MatiereResponseDto>> getById(
            @Parameter(description = "UUID de la matière") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(toDto(getMatiereUseCases.getMatiereById(id))));
    }

    @Operation(summary = "Créer une matière (multipart/form-data)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Matière créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<MatiereResponseDto>> create(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {

        String logoUrl = (logo != null && !logo.isEmpty()) ? minioService.uploadFile(logo, "matieres") : null;
        Matieres matieres = createMatiereUseCase.execute(name, description, isActive, logoUrl);
        return ResponseEntity.status(201).body(ApiResponseFormat.fromResponse(toDto(matieres)));
    }

    @Operation(summary = "Mettre à jour une matière (multipart/form-data)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Matière mise à jour"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Matière introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<MatiereResponseDto>> update(
            @Parameter(description = "UUID de la matière") @PathVariable UUID id,
            @RequestParam(value = "name",        required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isActive",    required = false) Boolean isActive,
            @RequestParam(value = "logo",        required = false) MultipartFile logo) {

        String logoUrl = (logo != null && !logo.isEmpty()) ? minioService.uploadFile(logo, "matieres") : null;
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                toDto(updateMatiereUseCase.execute(id, name, description, isActive, logoUrl))));
    }

    @Operation(summary = "Supprimer une matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Matière supprimée"),
            @ApiResponse(responseCode = "404", description = "Matière introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID de la matière") @PathVariable UUID id) {
        deleteMatiereUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    private MatiereResponseDto toDto(Matieres m) {
        return MatiereResponseDto.builder()
                .id(m.getId())
                .name(m.getName())
                .description(m.getDescription())
                .logoUrl(m.getLogoUrl())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .isActive(m.getIsActive())
                .metadata(m.getMetadata())
                .build();
    }
}
