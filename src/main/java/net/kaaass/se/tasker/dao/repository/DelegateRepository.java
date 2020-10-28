package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.DelegateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * 委托查询
 */
public interface DelegateRepository extends JpaRepository<DelegateEntity, String> {

    List<DelegateEntity> findAllByExpireBeforeAndExpireNotNull(Timestamp expire);

    boolean existsByTaskIdAndFromId(String task_id, String from_id);
}
