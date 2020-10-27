package net.kaaass.se.tasker.service;

import net.kaaass.se.tasker.dao.entity.UserEntity;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.UserNotFoundException;

import java.util.Optional;

/**
 * 用户相关服务
 */
public interface UserService {

    /**
     * 从 uid 获得用户数据
     */
    Optional<UserDto> getByUid(String uid);

    /**
     * 从 uid 获得用户实体
     */
    Optional<UserEntity> getEntity(String uid);

    /**
     * 删除用户
     */
    void remove(String id) throws NotFoundException;

    /**
     * 用户赋权
     */
    UserDto grant(String uid, String role) throws UserNotFoundException;

    /**
     * 取消用户赋权
     */
    UserDto revoke(String uid, String role) throws UserNotFoundException;
}
