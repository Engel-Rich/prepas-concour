package com.mutrix.prepa.application.dto.response.admin;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsResponse {
    private long total;
    private long active;
    private long inactive;
    private long newLast30Days;
    private long emailVerified;
    private long phoneVerified;
}
