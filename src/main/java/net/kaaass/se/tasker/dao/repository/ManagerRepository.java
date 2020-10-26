package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.ManagerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 经理查询
 */
public interface ManagerRepository extends JpaRepository<ManagerEntity, String> {

    Optional<ManagerEntity> findByUserId(String user_id);
}
