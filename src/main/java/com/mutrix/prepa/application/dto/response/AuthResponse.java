package com.mutrix.prepa.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    @Schema(description = "Les informations de l'utilisateur authentifié")
    private  UserResponse userResponse;

    @Schema(description = "Le custom Token de l'hautentifictaion", example = "xyzfuuohis.....")
    private  String token;
}
