package com.mutrix.prepa.domaines.models;

import java.util.UUID;

import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class Roles {
    @Setter(AccessLevel.NONE)
    private UUID id;

    private String name;

    public Roles(String name) {
        this.name = name;
    }
}
