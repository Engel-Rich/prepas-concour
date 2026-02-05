package com.mutrix.prepa.domaines.models;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class Cours {

    @Setter(AccessLevel.NONE)
    private UUID id;
    private String title;
    private String body;
    private String videoUrl;
    private UUID matiereId;
    private UUID userId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> metadata;

    public Cours(String title, String body, String videoUrl, UUID matiereId, UUID userId) {
        this.title = title;
        this.body = body;
        this.videoUrl = videoUrl;
        this.matiereId = matiereId;
        this.userId = userId;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Cours(
            UUID id,
            String title,
            UUID matiereId,
            String body
    ) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.matiereId = matiereId;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }
}
