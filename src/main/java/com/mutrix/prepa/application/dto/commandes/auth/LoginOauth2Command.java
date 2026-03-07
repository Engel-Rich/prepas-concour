package com.mutrix.prepa.application.dto.commandes.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public  class  LoginOauth2Command {

    @NotBlank(message = "OAuth2 provider token is required")
    @Schema(description = "Le token d'authentification fourni par le fournisseur OAuth2", example = "ya29.a0AfH6SMD...", requiredMode = Schema.RequiredMode.REQUIRED)
    private  String oAuth2ProviderToken;
}
