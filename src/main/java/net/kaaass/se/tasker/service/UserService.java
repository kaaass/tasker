package net.kaaass.se.tasker.service;

import net.kaaass.se.tasker.controller.request.UserRequest;
import net.kaaass.se.tasker.dao.entity.UserEntity;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.UserNotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.List;
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

    /**
     * 获取所有用户信息
     */
    List<UserDto> getAll(Pageable pageable);

    /**
     * 更新用户信息
     */
    UserDto update(String uid, UserRequest request) throws UserNotFoundException;

    /**
     * 创建用户
     * @return
     */
    Optional<UserDto> add(UserRequest request);
}
