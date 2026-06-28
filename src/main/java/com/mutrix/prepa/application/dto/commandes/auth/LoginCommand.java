package com.mutrix.prepa.application.dto.commandes.auth;

import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginCommand {

    @Schema(description = "Le numéro de téléphone de l'utilisateur", example = "+237 699 123 456", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String phoneNumber;

    @Schema(description = "Email de l'utilisateur", example = "johndoe@gmail.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;

    @NotNull(message = "Password is required")
    @Schema(description = "Le mot de passe de l'utilisateur (code PIN numérique)", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long password;

    @Schema(description = "Canal d'envoi du code OTP. Si omis : EMAIL si l'email est fourni, SMS sinon.", example = "EMAIL")
    private NotificationType notificationType;

    @AssertTrue(message = "Either phone number or email must be provided")
    public boolean isContactInfoProvided() {
        return (phoneNumber != null && !phoneNumber.isBlank())
                || (email != null && !email.isBlank());
    }
}
