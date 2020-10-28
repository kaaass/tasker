package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.ProjectEntity;
import net.kaaass.se.tasker.dao.entity.TaskEntity;
import net.kaaass.se.tasker.dto.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 任务查询
 */
public interface TaskRepository extends JpaRepository<TaskEntity, String> {

    boolean existsByProjectAndStatus(ProjectEntity project, TaskStatus status);

    boolean existsByProjectAndStatusIsNot(ProjectEntity project, TaskStatus status);
}
