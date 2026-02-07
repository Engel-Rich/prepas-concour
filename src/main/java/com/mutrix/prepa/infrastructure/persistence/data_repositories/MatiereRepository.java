package com.mutrix.prepa.infrastructure.persistence.data_repositories;

import com.mutrix.prepa.infrastructure.persistence.entities.MatiereEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MatiereRepository extends JpaRepository<MatiereEntity, UUID> {

}
