package com.mutrix.prepa.application.usecases.admin;

import com.mutrix.prepa.application.dto.response.admin.UserStatisticsResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@UseCase
@RequiredArgsConstructor
public class GetUserStatisticsUseCase {

    private final UserRepository userRepository;

    public UserStatisticsResponse execute() {
        long total = userRepository.count();
        long active = userRepository.countByIsActive(true);
        long inactive = userRepository.countByIsActive(false);
        long newLast30Days = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(30));
        long emailVerified = userRepository.countByHasEmailVerified(true);
        long phoneVerified = userRepository.countByHasPhoneVerified(true);

        return UserStatisticsResponse.builder()
                .total(total)
                .active(active)
                .inactive(inactive)
                .newLast30Days(newLast30Days)
                .emailVerified(emailVerified)
                .phoneVerified(phoneVerified)
                .build();
    }
}
