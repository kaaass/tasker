package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 任务查询
 */
public interface TaskRepository extends JpaRepository<TaskEntity, String> {
}
