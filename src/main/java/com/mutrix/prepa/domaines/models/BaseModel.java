package com.mutrix.prepa.domaines.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseModel {

    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isActive;

    private Map<String, Object> metadata;
}
