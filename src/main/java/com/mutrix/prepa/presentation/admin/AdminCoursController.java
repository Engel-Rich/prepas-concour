package com.mutrix.prepa.presentation.admin;

import com.mutrix.prepa.application.dto.commandes.cours.CreateCoursCommand;
import com.mutrix.prepa.application.dto.commandes.cours.UpdateCoursCommand;
import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.application.dto.response.cours.VideoEncryptionResponse;
import com.mutrix.prepa.application.usecases.cours.*;
import com.mutrix.prepa.infrastructure.crypto.VideoPreviewTokenService;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
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
    private final SearchCoursUseCase searchCoursUseCase;
    private final StreamCoursVideoUseCase streamCoursVideoUseCase;
    private final VideoPreviewTokenService videoPreviewTokenService;
    private final GetCoursByMatiereUseCase getCoursByMatiereUseCase;
    private final DeleteCoursUseCase deleteCoursUseCase;
    private final EncryptCoursVideoUseCase encryptCoursVideoUseCase;
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

        // Le cours est créé d'abord : la clé de contenu est rattachée à son id.
        CreateCoursCommand command = CreateCoursCommand.builder()
                .title(title)
                .body(body)
                .videoUrl(null)
                .matiereId(matiereId)
                .isActive(isActive)
                .gratuit(gratuit)
                .build();

        CoursResponse created = createCoursUseCase.execute(command, userId);

        // Puis la vidéo est chiffrée et déposée. Un échec de chiffrement
        // n'empêche pas le dépôt : la vidéo part en clair et le motif est
        // conservé pour la console.
        if (file != null && !file.isEmpty()) {
            created = attachVideo(created.getId(), file);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseFormat.fromResponse(created));
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

        UpdateCoursCommand command = UpdateCoursCommand.builder()
                .title(title)
                .body(body)
                .videoUrl(null)   // null → conserve l'URL existante dans le use case
                .matiereId(matiereId)
                .isActive(isActive)
                .gratuit(gratuit)
                .build();

        CoursResponse updated = updateCoursUseCase.execute(id, command, userId);

        // Nouvelle vidéo : elle remplace l'ancienne, chiffrée si possible.
        if (file != null && !file.isEmpty()) {
            updated = attachVideo(id, file);
        }

        return ResponseEntity.ok(ApiResponseFormat.fromResponse(updated));
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
            @Parameter(description = "Filtrer sur une matière") @RequestParam(required = false) UUID matiereId,
            @Parameter(description = "Filtrer sur une session de concours") @RequestParam(required = false) UUID sessionId,
            @Parameter(description = "Filtrer sur l'état de chiffrement") @RequestParam(required = false) Boolean hasBeenCrypted,
            @Parameter(description = "Recherche sur le titre") @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        // Les filtres sont cumulables et appliqués en base : filtrer la seule
        // page courante côté navigateur donnerait des résultats faux.
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                searchCoursUseCase.execute(matiereId, sessionId, hasBeenCrypted, search, page, size)));
    }

    @Operation(summary = "Obtenir un jeton de prévisualisation",
            description = "Délivre un jeton signé, valable quelques minutes, permettant au lecteur "
                    + "du navigateur de lire la vidéo déchiffrée sans en-tête d'authentification.")
    @PostMapping("/{id}/video-preview-token")
    public ResponseEntity<ApiResponseFormat<Map<String, String>>> videoPreviewToken(
            @Parameter(description = "UUID du cours") @PathVariable UUID id) {
        String token = videoPreviewTokenService.issue(id);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                Map.of("token", token, "path", "/cours-preview/" + id)));
    }

    @Operation(summary = "Prévisualiser la vidéo d'un cours",
            description = "Renvoie la vidéo en clair, déchiffrée à la volée si nécessaire. "
                    + "Supporte les requêtes Range pour permettre la navigation dans le lecteur. "
                    + "La clé de contenu ne quitte jamais le serveur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flux complet"),
            @ApiResponse(responseCode = "206", description = "Plage partielle"),
            @ApiResponse(responseCode = "404", description = "Cours ou vidéo introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @GetMapping("/{id}/video-stream")
    public void streamVideo(
            @Parameter(description = "UUID du cours") @PathVariable UUID id,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletResponse response) throws Exception {

        StreamCoursVideoUseCase.StreamInfo info = streamCoursVideoUseCase.describe(id);
        long total = info.totalSize();

        long from = 0;
        long to = total - 1;
        boolean partial = false;

        // Range: bytes=start-[end] — le lecteur du navigateur l'envoie dès
        // qu'il veut se déplacer dans la vidéo.
        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] bounds = rangeHeader.substring(6).split("-", 2);
            try {
                from = Long.parseLong(bounds[0].trim());
                if (bounds.length > 1 && !bounds[1].isBlank()) {
                    to = Math.min(Long.parseLong(bounds[1].trim()), total - 1);
                }
                partial = true;
            } catch (NumberFormatException ignored) {
                // En-tête illisible : on sert le fichier entier.
            }
        }

        if (from >= total || from > to) {
            response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            response.setHeader("Content-Range", "bytes */" + total);
            return;
        }

        response.setStatus(partial
                ? HttpServletResponse.SC_PARTIAL_CONTENT
                : HttpServletResponse.SC_OK);
        response.setContentType(info.contentType());
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(to - from + 1));
        if (partial) {
            response.setHeader("Content-Range", "bytes " + from + "-" + to + "/" + total);
        }

        streamCoursVideoUseCase.writeRange(id, info, from, to, response);
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

    @Operation(summary = "Chiffrer la vidéo d'un cours",
            description = "Chiffre une vidéo déposée en clair et remplace l'objet dans le bucket. "
                    + "Opération rejouable : en cas d'échec, la vidéo reste accessible en clair et "
                    + "le motif est conservé dans le champ encryptionError du cours.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opération effectuée — voir le champ outcome"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    @PostMapping("/{id}/encrypt-video")
    public ResponseEntity<ApiResponseFormat<VideoEncryptionResponse>> encryptVideo(
            @Parameter(description = "UUID du cours") @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(
                encryptCoursVideoUseCase.execute(id)));
    }

    /** Dépose et chiffre la vidéo d'un cours déjà créé. */
    private CoursResponse attachVideo(UUID coursId, MultipartFile file) {
        return encryptCoursVideoUseCase.attachVideo(coursId, file);
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
