package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.commandes.cours.CreateCoursCommand;
import com.mutrix.prepa.application.dto.commandes.cours.UpdateCoursCommand;
import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.application.usecases.cours.*;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
import com.mutrix.prepa.infrastructure.services.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/admin/cours")
@RequiredArgsConstructor
@Tag(name = "Admin - Cours", description = "Gestion des cours par l'administrateur")
public class AdminCoursController {

    private final CreateCoursUseCase createCoursUseCase;
    private final UpdateCoursUseCase updateCoursUseCase;
    private final GetCoursUseCase getCoursUseCase;
    private final GetAllCoursUseCase getAllCoursUseCase;
    private final GetCoursByMatiereUseCase getCoursByMatiereUseCase;
    private final DeleteCoursUseCase deleteCoursUseCase;
    private final MinioService minioService;

    private UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((SecurityUser) auth.getPrincipal()).getUser().getId();
    }

    @Operation(summary = "Créer un cours",
            description = "Crée un cours avec un fichier optionnel uploadé sur MinIO (multipart/form-data)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cours créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<CoursResponse>> create(
            @RequestParam("title") String title,
            @RequestParam("body") String body,
            @RequestParam("matiereId") UUID matiereId,
            @RequestParam(value = "isActive", required = false, defaultValue = "true") Boolean isActive,
            @RequestParam(value = "gratuit", required = false, defaultValue = "false") Boolean gratuit,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        UUID userId = getAuthenticatedUserId();

        // Upload file to MinIO if provided
        String videoUrl = null;
        if (file != null && !file.isEmpty()) {
            videoUrl = minioService.uploadFile(file, "cours");
        }

        CreateCoursCommand command = CreateCoursCommand.builder()
                .title(title)
                .body(body)
                .videoUrl(videoUrl)
                .matiereId(matiereId)
                .isActive(isActive)
                .gratuit(gratuit)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponse(createCoursUseCase.execute(command, userId)));
    }

    @Operation(summary = "Mettre à jour un cours",
            description = "Met à jour un cours. Si un fichier est fourni, il remplace le fichier existant sur MinIO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours mis à jour"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseFormat<CoursResponse>> update(
            @Parameter(description = "UUID du cours") @PathVariable UUID id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "body", required = false) String body,
            @RequestParam(value = "matiereId", required = false) UUID matiereId,
            @RequestParam(value = "isActive", required = false) Boolean isActive,
            @RequestParam(value = "gratuit", required = false) Boolean gratuit,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        UUID userId = getAuthenticatedUserId();

        // Upload new file to MinIO only if a new file is provided
        String videoUrl = null;
        if (file != null && !file.isEmpty()) {
            videoUrl = minioService.uploadFile(file, "cours");
        }

        UpdateCoursCommand command = UpdateCoursCommand.builder()
                .title(title)
                .body(body)
                .videoUrl(videoUrl)   // null → keep existing URL in use case
                .matiereId(matiereId)
                .isActive(isActive)
                .gratuit(gratuit)
                .build();

        return ResponseEntity.ok(ApiResponseFormat.fromResponse(updateCoursUseCase.execute(id, command, userId)));
    }

    @Operation(summary = "Récupérer un cours par ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours trouvé"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<CoursResponse>> getById(
            @Parameter(description = "UUID du cours") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getCoursUseCase.execute(id, null)));
    }

    @Operation(summary = "Lister tous les cours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<PageResponse<CoursResponse>>> listAll(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getAllCoursUseCase.execute(page, size)));
    }

    @Operation(summary = "Lister les cours par matière")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste récupérée"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<ApiResponseFormat<PageResponse<CoursResponse>>> getByMatiere(
            @Parameter(description = "UUID de la matière") @PathVariable UUID matiereId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(getCoursByMatiereUseCase.execute(matiereId, pageable)));
    }

    @Operation(summary = "Supprimer un cours")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cours supprimé"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "UUID du cours") @PathVariable UUID id) {
        UUID userId = getAuthenticatedUserId();
        deleteCoursUseCase.execute(id, userId);
        return ResponseEntity.noContent().build();
    }
}
