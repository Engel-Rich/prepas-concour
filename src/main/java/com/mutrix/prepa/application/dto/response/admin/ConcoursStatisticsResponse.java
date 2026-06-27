package com.mutrix.prepa.application.dto.response.admin;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConcoursStatisticsResponse {
    private long totalConcours;
    private long activeConcours;
    private long totalSessions;
    private long activeSessions;
}
