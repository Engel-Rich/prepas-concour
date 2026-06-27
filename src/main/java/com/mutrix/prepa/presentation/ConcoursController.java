package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.response.ConcourResponseDTO;
import com.mutrix.prepa.application.usecases.concours.GetConcoursUseCases;
import com.mutrix.prepa.cors.ApiResponseFormat;
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
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(path = "/concours")
@Tag(description = "Gestion des concours et des sessions des concours", name = "Concours management")
@RequiredArgsConstructor
public class ConcoursController {
    private final GetConcoursUseCases getConcoursUseCases;

    @Operation(
            summary = "Récupérer un concours par son identifiant",
            description = "Retourne les informations détaillées d'un concours à partir de son UUID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Concours trouvé avec succès", content = @Content(schema = @Schema(implementation = ConcourResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Concours non trouvé"),
            @ApiResponse(responseCode = "400", description = "Identifiant invalide")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseFormat<ConcourResponseDTO>> getById(
            @Parameter(description = "Identifiant UUID du concours", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable(name = "id") String id
    ) {
        ConcourResponseDTO concourResponseDTO = getConcoursUseCases.getById(id);
        return ResponseEntity.ok().body(ApiResponseFormat.fromResponse(concourResponseDTO));
    }

    @Operation(
            summary = "Lister les concours avec pagination",
            description = "Retourne une liste paginée des concours disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des concours récupérée avec succès", content = @Content(schema = @Schema(implementation = ConcourResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres de pagination invalides")
    })
    @GetMapping
    public ResponseEntity<ApiResponseFormat<Page<ConcourResponseDTO>>> list(
            @Parameter(description = "Numéro de page (commence à 1)", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "Nombre d'éléments par page", example = "20")
            @RequestParam(defaultValue = "20") Integer size
    ) {
        Page<ConcourResponseDTO> concourResponseDTOPage =
                getConcoursUseCases.list(page == null ? 1 : page, size == 0 ? 25 : size);
        return ResponseEntity.ok().body(ApiResponseFormat.fromResponse(concourResponseDTOPage));
    }
}
