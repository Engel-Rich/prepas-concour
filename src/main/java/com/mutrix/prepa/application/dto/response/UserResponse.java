package com.mutrix.prepa.application.dto.response;

import com.mutrix.prepa.domaines.models.Roles;
import com.mutrix.prepa.domaines.valueobjects.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @Schema(description = "email du user")
    private String email;
    @Schema(description =  "Le nom complet de l'utilisateur", example = "John Doe")
    private String fullName;

    @Schema(description = "Le numéro de téléphone de l'utilisateur", example = "+237 699 123 456")
    private String phone;

    @Schema(description = "Le firebase Uid du user", example = "+237 699 123 456")
    private String firebaseUid;

    @Schema(description = "L'ID principale du user ", example = "qwkh2-18136qwasyg...", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID id;

    @Schema(description = "Les roldes de l'utilisateur", example = "[ADMIN, USER,TEACHER]")
    private List<Roles> roles;

    @Schema(description = "si l'utilisateur a deja un mot de passe", example = "false")
    private  Boolean hasPassword;

    @Schema()
    private Boolean hasEmailVerified;

    @Schema()
    private Boolean hasPhoneVerified;

    @Schema()
    private Boolean hasProfileCompleted;

    @Schema()
    private Map<String, Object> metadata;

    @Schema(description = "URL de la photo de profil", nullable = true)
    private String profilePictureUrl;

    @Schema(description = "Statut actif/inactif du compte")
    private Boolean isActive;

    @Schema(description = "Date d'inscription")
    private LocalDateTime createdAt;
}
