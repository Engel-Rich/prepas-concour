package com.mutrix.prepa.application.dto.response.admin;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GlobalStatisticsResponse {
    private UserStatisticsResponse users;
    private ConcoursStatisticsResponse concours;
    private SubscriptionStatisticsResponse subscriptions;
    private long totalCours;
    private long totalMatieres;
}
