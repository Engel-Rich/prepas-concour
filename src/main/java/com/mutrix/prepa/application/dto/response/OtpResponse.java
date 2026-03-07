package com.mutrix.prepa.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Builder
@Getter
public class OtpResponse {

    @Schema(description = "L'identifiant de la session OTP", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID otpId;

    @Schema(description = "Le message indiquant que le code OTP a été envoyé avec succès", example = "OTP code sent successfully")
    private String email;

    @Schema(description = "Le message indiquant que le code OTP a été envoyé avec succès", example = "OTP code sent successfully")
    private String phoneNumber;

    @Schema(description = "Le message indiquant que le code OTP a été envoyé avec succès", example = "12345689")
    private Long expirationTime;
}
