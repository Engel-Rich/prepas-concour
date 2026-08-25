package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.usecases.cours.StreamCoursVideoUseCase;
import com.mutrix.prepa.infrastructure.crypto.VideoPreviewTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Lecture d'une vidéo par jeton de prévisualisation.
 *
 * <p>Route volontairement hors de {@code /admin} : une balise {@code <video>}
 * ne peut pas porter d'en-tête {@code Authorization}. L'autorisation repose
 * donc sur un jeton signé, à durée de vie courte, délivré à un administrateur
 * authentifié via {@code POST /admin/cours/{id}/video-preview-token}.
 *
 * <p>Le serveur déchiffre à la volée : la clé de contenu ne quitte jamais le
 * serveur, contrairement au mobile qui la reçoit pour la lecture hors ligne.
 */
@Slf4j
@RestController
@RequestMapping("/cours-preview")
@RequiredArgsConstructor
@Tag(name = "Prévisualisation vidéo", description = "Lecture par jeton signé")
public class VideoPreviewController {

    private final VideoPreviewTokenService tokenService;
    private final StreamCoursVideoUseCase streamCoursVideoUseCase;

    @Operation(summary = "Lire une vidéo avec un jeton de prévisualisation")
    @GetMapping("/{id}")
    public void preview(
            @PathVariable UUID id,
            @RequestParam("token") String token,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletResponse response) throws Exception {

        if (!tokenService.isValid(token, id)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        StreamCoursVideoUseCase.StreamInfo info = streamCoursVideoUseCase.describe(id);
        long total = info.totalSize();

        long from = 0;
        long to = total - 1;
        boolean partial = false;

        // Le lecteur du navigateur envoie « Range: bytes=start-[end] » dès
        // qu'il démarre ou que l'utilisateur se déplace dans la vidéo.
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
        // Contenu protégé : aucun intermédiaire ne doit le conserver.
        response.setHeader("Cache-Control", "no-store, private");
        if (partial) {
            response.setHeader("Content-Range", "bytes " + from + "-" + to + "/" + total);
        }

        streamCoursVideoUseCase.writeRange(id, info, from, to, response);
    }
}
