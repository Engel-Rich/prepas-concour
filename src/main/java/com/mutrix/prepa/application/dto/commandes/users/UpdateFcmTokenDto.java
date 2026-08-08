package com.mutrix.prepa.application.dto.commandes.users;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateFcmTokenDto {
    @NotBlank
    private String fcmToken;
}
