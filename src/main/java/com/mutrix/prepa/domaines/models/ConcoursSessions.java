package com.mutrix.prepa.domaines.models;

import com.mutrix.prepa.domaines.valueobjects.ConcoursSessionsStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ConcoursSessions {

    @Setter(AccessLevel.NONE)
    private UUID id;
    private UUID concoursId;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private  Double amount;
    private ConcoursSessionsStatus status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;

    public ConcoursSessions(UUID concoursId, String name, String description, LocalDate startDate, LocalDate endDate) {
        this.concoursId = concoursId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ConcoursSessions(UUID id, UUID concoursId, String name, String description, LocalDate startDate,
            LocalDate endDate) {
        this.id = id;
        this.concoursId = concoursId;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
}
