package net.kaaass.se.tasker.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.TaskerApplication;
import net.kaaass.se.tasker.controller.request.UserRegisterRequest;
import net.kaaass.se.tasker.controller.response.LoginResponse;
import net.kaaass.se.tasker.dao.entity.UserEntity;
import net.kaaass.se.tasker.dao.repository.UserAuthRepository;
import net.kaaass.se.tasker.dto.AuthTokenDto;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.event.UserRegisterEvent;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.ServiceUnavailableException;
import net.kaaass.se.tasker.mapper.UserMapper;
import net.kaaass.se.tasker.security.JwtTokenUtil;
import net.kaaass.se.tasker.service.AuthService;
import net.kaaass.se.tasker.service.EmployeeService;
import net.kaaass.se.tasker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    private UserAuthRepository repository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Optional<UserDto> register(UserRegisterRequest userToAdd) throws BadRequestException {
        // 账户注册
        UserDto result;
        try {
            var request = mapper.mapUserRequest(userToAdd);
            result = userService.add(request).orElseThrow();
        } catch (Exception e) {
            return Optional.empty();
        }
        // 员工信息注册
        try {
            var request = mapper.mapEmployeeRequest(userToAdd);
            request.setUid(result.getId());
            result = employeeService.add(request).getUser();
        } catch (NotFoundException e) {
            log.error("注册过程发生异常", e);
            return Optional.empty();
        } catch (BadRequestException e) {
            // TODO 更改为事务
            try {
                userService.remove(result.getId());
            } catch (NotFoundException ignore) {
            }
            throw e;
        }
        // 触发事件
        TaskerApplication.EVENT_BUS.post(new UserRegisterEvent(result));
        return Optional.of(result);
    }

    @Override
    public Optional<LoginResponse> login(String username, String password) {
        try {
            var uid = repository.findByUsername(username)
                    .map(UserEntity::getId)
                    .orElseThrow();
            var upToken = new UsernamePasswordAuthenticationToken(uid, password);
            // 登录验证
            var auth = authenticationManager.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            var userDetails = (UserDetails) auth.getPrincipal();
            // 更新时间
            repository.findById(uid)
                    .ifPresent(userAuthEntity -> {
                        userAuthEntity.setLastLoginTime(Timestamp.valueOf(LocalDateTime.now()));
                        repository.save(userAuthEntity);
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
    public Optional<AuthTokenDto> refresh(String oldToken) {
        return Optional.of(oldToken)
                .filter(this::validateTokenViaDatabase)
                .flatMap(jwtTokenUtil::refreshToken);
    }

    private boolean validateTokenViaDatabase(String oldToken) {
        var username = jwtTokenUtil.getUsernameFromToken(oldToken); // 顺便校验了格式
        var jwtUser = username.map(userDetailsService::loadUserByUsername);
        return jwtUser.filter(userDetails -> jwtTokenUtil.validateToken(oldToken, userDetails))
                .isPresent();
    }
}
