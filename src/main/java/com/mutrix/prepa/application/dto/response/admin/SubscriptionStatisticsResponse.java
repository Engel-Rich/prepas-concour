package com.mutrix.prepa.application.dto.response.admin;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionStatisticsResponse {
    private long total;
    private long active;
    private long pending;
    private long cancelled;
    private long expired;
    private long newLast30Days;
}
