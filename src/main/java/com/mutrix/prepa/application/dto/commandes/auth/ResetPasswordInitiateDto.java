package com.mutrix.prepa.application.dto.commandes.auth;

import com.mutrix.prepa.domaines.valueobjects.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordInitiateDto {

    @Schema(description = "Email de l'utilisateur", example = "johndoe@gmail.com")
    private String email;

    @Schema(description = "Numéro de téléphone de l'utilisateur", example = "+237 699 123 456")
    private String phoneNumber;

    @Schema(description = "Canal d'envoi du code OTP. Si omis : EMAIL si l'email est fourni, SMS sinon.", example = "EMAIL")
    private NotificationType notificationType;

    @AssertTrue(message = "Email ou numéro de téléphone requis")
    public boolean isContactInfoProvided() {
        return (email != null && !email.isBlank())
                || (phoneNumber != null && !phoneNumber.isBlank());
    }
}
