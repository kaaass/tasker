package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.TaskerApplication;
import net.kaaass.se.tasker.controller.request.UserRegisterRequest;
import net.kaaass.se.tasker.controller.response.LoginResponse;
import net.kaaass.se.tasker.dao.entity.UserAuthEntity;
import net.kaaass.se.tasker.dao.repository.UserAuthRepository;
import net.kaaass.se.tasker.dto.AuthTokenDto;
import net.kaaass.se.tasker.dto.UserAuthDto;
import net.kaaass.se.tasker.event.UserRegisterEvent;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.mapper.UserMapper;
import net.kaaass.se.tasker.security.JwtTokenUtil;
import net.kaaass.se.tasker.service.AuthService;
import net.kaaass.se.tasker.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 用户鉴权相关服务的具体实现
 */
@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Qualifier("jwtUserDetailsServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserAuthRepository authRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Optional<UserAuthDto> register(UserRegisterRequest userToAdd) {
        // 登录信息
        var authEntity = new UserAuthEntity();
        authEntity.setUsername(userToAdd.getUsername());
        authEntity.setPassword(jwtTokenUtil.encryptPassword(userToAdd.getPassword()));
        authEntity.setRoles(Constants.ROLE_USER);
        try {
            authEntity = authRepository.save(authEntity);
        } catch (Exception e) {
            return Optional.empty();
        }
        // TODO 注册增加职工信息，使用参数 name，或者直接转移到 EmployeeService::register
        // 拼接结果
        var result = userMapper.userAuthEntityToDto(authEntity);
        // 触发事件
        TaskerApplication.EVENT_BUS.post(new UserRegisterEvent(result));
        return Optional.of(result);
    }

    @Override
    public Optional<LoginResponse> login(String username, String password) {
        try {
            var uid = authRepository.findByUsername(username)
                    .map(UserAuthEntity::getId)
                    .orElseThrow();
            var upToken = new UsernamePasswordAuthenticationToken(uid, password);
            // 登录验证
            var auth = authenticationManager.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            var userDetails = (UserDetails) auth.getPrincipal();
            // 更新时间
            authRepository.findById(uid)
                    .ifPresent(userAuthEntity -> {
                        userAuthEntity.setLastLoginTime(Timestamp.valueOf(LocalDateTime.now()));
                        authRepository.save(userAuthEntity);
                    });

            // 拼接凭据
            return Optional.of(userDetails)
                    .map(jwtUser -> {
                        var roles = jwtUser.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList());
                        var authTokenDto = jwtTokenUtil.generateToken(jwtUser);

                        return new LoginResponse(authTokenDto, username, roles);
                    });
        } catch (AuthenticationException e) {
            log.info("登录失败", e);
            return Optional.empty();
        } catch (NoSuchElementException e) {
            log.info("账户 {} 不存在", username);
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserAuthDto> getByUid(String uid) {
        return authRepository.findById(uid)
                .map(userMapper::userAuthEntityToDto);
    }

    @Override
    public Optional<UserAuthEntity> getEntity(String uid) {
        return authRepository.findById(uid);
    }

    @Override
    public Optional<AuthTokenDto> refresh(String oldToken) {
        return Optional.of(oldToken)
                .filter(this::validateTokenViaDatabase)
                .flatMap(jwtTokenUtil::refreshToken);
    }

    @Override
    public void remove(String id) throws NotFoundException {
        var entity = authRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("未找到该用户！"));
        // TODO 删除对应职工信息，或者可以用级联删除，或者直接转移到 EmployeeService
        authRepository.delete(entity);
    }

    private boolean validateTokenViaDatabase(String oldToken) {
        var username = jwtTokenUtil.getUsernameFromToken(oldToken); // 顺便校验了格式
        var jwtUser = username.map(userDetailsService::loadUserByUsername);
        return jwtUser.filter(userDetails -> jwtTokenUtil.validateToken(oldToken, userDetails))
                .isPresent();
    }
}
