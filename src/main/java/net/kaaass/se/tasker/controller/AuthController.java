package net.kaaass.se.tasker.controller;

import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.controller.request.UserRegisterRequest;
import net.kaaass.se.tasker.controller.response.LoginResponse;
import net.kaaass.se.tasker.dto.AuthTokenDto;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.ServiceUnavailableException;
import net.kaaass.se.tasker.mapper.UserMapper;
import net.kaaass.se.tasker.service.AuthService;
import net.kaaass.se.tasker.service.EmployeeService;
import net.kaaass.se.tasker.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 鉴权相关接口
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {
    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthService service;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录并获取用户鉴权令牌
     */
    @PostMapping("/login")
    public LoginResponse createAuthenticationToken(
            @RequestParam String username, @RequestParam String password) throws BadRequestException {
        // TODO 检查输入
        return service.login(username, password)
                .orElseThrow(() -> new BadRequestException("用户名或密码错误！"));
    }

    /**
     * 刷新当前用户的鉴权令牌
     */
    @GetMapping("/refresh")
    @PreAuthorize("authenticated")
    public AuthTokenDto refreshAndGetAuthenticationToken(HttpServletRequest request) throws BadRequestException {
        String token = request.getHeader(tokenHeader);
        return service.refresh(token)
                .orElseThrow(() -> new BadRequestException("该Token无效！"));
    }

    /**
     * 用于员工的注册
     */
    @PostMapping("/register")
    public UserVo register(@RequestBody UserRegisterRequest addedUser) throws BadRequestException {
        // 用户注册
        var user = service.register(addedUser)
                .orElseThrow(() -> new BadRequestException("该用户名已被注册！"));
        return mapper.dtoToVo(user);
    }
}
