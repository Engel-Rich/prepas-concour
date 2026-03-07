package com.mutrix.prepa.domaines.models;

import com.mutrix.prepa.domaines.valueobjects.OtpType;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OtpSession {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String otpHash;
    private Long expiresAt;
    private OtpType otpType;
    private Map<String, Object> metadata;
}
