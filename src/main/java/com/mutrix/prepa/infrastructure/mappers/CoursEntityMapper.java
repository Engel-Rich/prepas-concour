package com.mutrix.prepa.infrastructure.mappers;

import com.mutrix.prepa.cors.MetadataMapper;
import com.mutrix.prepa.domaines.models.Cours;
import com.mutrix.prepa.infrastructure.persistence.entities.CoursEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.MatiereEntity;
import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;

import java.util.Map;

public final class CoursEntityMapper {

    private CoursEntityMapper() {
        // Private constructor to prevent instantiation
    }

    public  static Cours toDCoursDomain(CoursEntity entity){
        if(entity == null){
            return null;
        }
        Map<String, Object> metadata = MetadataMapper.mapFromJsonToMap(entity.getMetadata());
        return Cours.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .body(entity.getBody())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isActive(entity.getIsActive())
                .videoUrl(entity.getVideoUrl())
                .matiereId(entity.getMatiere().getId())
                .userId(entity.getUser().getId())
                .metadata(metadata)
                .build();
    }

    public static CoursEntity toCoursEntity(Cours cours, MatiereEntity matiereEntity, UserEntity userEntity){
        if(cours == null){
            return null;
        }
        String metadata = MetadataMapper.mapToJson(cours.getMetadata());

        return CoursEntity.builder()
                .id(cours.getId())
                .title(cours.getTitle())
                .body(cours.getBody())
                .createdAt(cours.getCreatedAt())
                .updatedAt(cours.getUpdatedAt())
                .isActive(cours.getIsActive())
                .videoUrl(cours.getVideoUrl())
                .matiere(matiereEntity)
                .user(userEntity)
                .metadata(metadata)
                .build();
    }
}
