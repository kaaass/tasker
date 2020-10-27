package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.dao.entity.UserEntity;
import net.kaaass.se.tasker.dao.repository.UserAuthRepository;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.UserNotFoundException;
import net.kaaass.se.tasker.mapper.UserMapper;
import net.kaaass.se.tasker.service.UserService;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户相关服务的具体实现
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserAuthRepository repository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Optional<UserDto> getByUid(String uid) {
        return repository.findById(uid)
                .map(userMapper::entityToDto);
    }

    @Override
    public Optional<UserEntity> getEntity(String uid) {
        return repository.findById(uid);
    }

    @Override
    public void remove(String id) throws NotFoundException {
        var entity = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("未找到该用户！"));
        repository.delete(entity);
    }

    @Override
    public UserDto grant(String uid, String role) throws UserNotFoundException {
        var entity = getEntity(uid).orElseThrow(UserNotFoundException::new);
        var roles = Collections.asSet(entity.getRoles().split(","));
        roles.add(role);
        entity.setRoles(String.join(",", roles));
        repository.save(entity);
        return userMapper.entityToDto(entity);
    }

    @Override
    public UserDto revoke(String uid, String role) throws UserNotFoundException {
        var entity = getEntity(uid).orElseThrow(UserNotFoundException::new);
        var roles = Collections.asSet(entity.getRoles().split(","));
        roles.remove(role);
        entity.setRoles(String.join(",", roles));
        repository.save(entity);
        return userMapper.entityToDto(entity);
    }
}
