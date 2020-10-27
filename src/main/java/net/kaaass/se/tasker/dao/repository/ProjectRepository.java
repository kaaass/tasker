package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 项目查询
 */
public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {
}
