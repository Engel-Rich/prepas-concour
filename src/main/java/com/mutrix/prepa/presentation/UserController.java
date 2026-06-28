package com.mutrix.prepa.presentation;

import com.mutrix.prepa.application.dto.commandes.auth.UpdatePasswordDto;
import com.mutrix.prepa.application.usecases.auth.UpdatePasswordUseCase;
import com.mutrix.prepa.cors.ApiResponseFormat;
import com.mutrix.prepa.infrastructure.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs", description = "Opérations sur le compte de l'utilisateur connecté")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UpdatePasswordUseCase updatePasswordUseCase;

    @Operation(
        summary = "Mettre à jour le mot de passe",
        description = "L'utilisateur authentifié envoie son ancien et son nouveau code PIN pour mettre à jour son mot de passe (en base et sur Firebase).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect ou données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @PutMapping("/password/update")
    public ResponseEntity<ApiResponseFormat<Void>> updatePassword(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody @Valid UpdatePasswordDto dto) {

        if (securityUser == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }
        updatePasswordUseCase.execute(securityUser.getUser(), dto);
        return ResponseEntity.ok(ApiResponseFormat.fromResponse(null));
    }
}
