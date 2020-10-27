package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.DelegateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 委托查询
 */
public interface DelegateRepository extends JpaRepository<DelegateEntity, String> {
}
