package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.ManagerEntity;
import net.kaaass.se.tasker.dao.entity.ProjectEntity;
import net.kaaass.se.tasker.dto.ProjectStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 项目查询
 */
public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {

    boolean existsByIdAndUndertaker(String id, ManagerEntity undertaker);

    @Query("select p from EmployeeEntity e join TaskEntity t on e.id = t.undertaker join ProjectEntity p on p.id = t.project where e.id = :eid")
    List<ProjectEntity> findAllOfEmployee(@Param("eid") String eid, Pageable pageable);

    long countAllByStatus(ProjectStatus status);
}
