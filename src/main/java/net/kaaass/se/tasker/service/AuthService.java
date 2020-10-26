package net.kaaass.se.tasker.service;

import net.kaaass.se.tasker.controller.request.UserRegisterRequest;
import net.kaaass.se.tasker.controller.response.LoginResponse;
import net.kaaass.se.tasker.dao.entity.UserAuthEntity;
import net.kaaass.se.tasker.dto.AuthTokenDto;
import net.kaaass.se.tasker.dto.UserAuthDto;
import net.kaaass.se.tasker.exception.NotFoundException;

import java.util.Optional;

/**
 * 用户鉴权相关服务
 */
public interface AuthService {
    /**
     * 用户注册
     */
    Optional<UserAuthDto> register(UserRegisterRequest userToAdd);

    /**
     * 用户登录
     */
    Optional<LoginResponse> login(String username, String password);

    /**
     * 从 uid 获得用户数据
     */
    Optional<UserAuthDto> getByUid(String uid);

    /**
     * 从 uid 获得用户实体
     */
    Optional<UserAuthEntity> getEntity(String uid);

    /**
     * 令牌刷新
     */
    Optional<AuthTokenDto> refresh(String oldToken);

    /**
     * 删除用户
     */
    void remove(String id) throws NotFoundException;
}