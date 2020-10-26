package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 员工查询
 */
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {

    Optional<EmployeeEntity> findByUserId(String user_id);
}
