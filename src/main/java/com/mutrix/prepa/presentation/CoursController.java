package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.response.CoursResponse;
import com.mutrix.prepa.application.usecases.cours.GetAllCoursUseCase;
import com.mutrix.prepa.application.usecases.cours.GetCoursByMatiereUseCase;
import com.mutrix.prepa.application.usecases.cours.GetCoursUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.cors.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Obtenir un cours par ID", description = "Retourne les détails d'un cours spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cours trouvé"),
            @ApiResponse(responseCode = "404", description = "Cours introuvable"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<CoursResponse>> getCoursById(@PathVariable UUID id) {
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
}
