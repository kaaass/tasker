package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.ResourceEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<ResourceEntity, String> {
    List<ResourceEntity> findAllByOrderByUploadTimeDesc(Pageable pageable);
}
