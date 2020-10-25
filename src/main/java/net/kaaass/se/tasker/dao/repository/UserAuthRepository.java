package net.kaaass.se.tasker.dao.repository;

import net.kaaass.se.tasker.dao.entity.UserAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户鉴权查询
 */
public interface UserAuthRepository extends JpaRepository<UserAuthEntity, String> {

    Optional<UserAuthEntity> findByUsername(String username);
}
