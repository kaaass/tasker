package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.controller.request.UserRequest;
import net.kaaass.se.tasker.dao.entity.UserEntity;
import net.kaaass.se.tasker.dao.repository.UserAuthRepository;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.UserNotFoundException;
import net.kaaass.se.tasker.mapper.UserMapper;
import net.kaaass.se.tasker.security.JwtTokenUtil;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.UserService;
import org.mapstruct.ap.internal.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户相关服务的具体实现
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserAuthRepository repository;

    @Autowired
    private UserMapper mapper;

    @Override
    public Optional<UserDto> getByUid(String uid) {
        return repository.findById(uid)
                .map(mapper::entityToDto);
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
        return mapper.entityToDto(entity);
    }

    @Override
    public UserDto revoke(String uid, String role) throws UserNotFoundException {
        var entity = getEntity(uid).orElseThrow(UserNotFoundException::new);
        var roles = Collections.asSet(entity.getRoles().split(","));
        roles.remove(role);
        entity.setRoles(String.join(",", roles));
        repository.save(entity);
        return mapper.entityToDto(entity);
    }

    @Override
    public List<UserDto> getAll(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(String uid, UserRequest request) throws UserNotFoundException {
        var entity = getEntity(uid).orElseThrow(UserNotFoundException::new);
        entity.setUsername(request.getUsername());
        entity.setPassword(jwtTokenUtil.encryptPassword(request.getPassword()));
        var result = repository.save(entity);
        return mapper.entityToDto(result);
    }

    @Override
    public Optional<UserDto> add(UserRequest request) {
        var entity = new UserEntity();
        entity.setUsername(request.getUsername());
        entity.setPassword(jwtTokenUtil.encryptPassword(request.getPassword()));
        entity.setRoles(Role.USER);
        try {
            entity = repository.saveAndFlush(entity);
        } catch (Exception e) {
            log.info("Err", e);
            return Optional.empty();
        }
        return Optional.of(mapper.entityToDto(entity));
    }
}
