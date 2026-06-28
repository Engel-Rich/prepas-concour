package com.mutrix.prepa.application.dto.commandes.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginOauth2Command {

    @NotBlank(message = "Le Firebase UID est requis")
    @Schema(
        description = "UID Firebase de l'utilisateur (credential.user.uid après signInWithPopup). " +
                      "Le Firebase ID Token doit être envoyé dans le header Authorization: Bearer <idToken>.",
        example = "C6rdlSeg2vhXbqXpB0jbXGyxYv02",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firebaseUid;
}
