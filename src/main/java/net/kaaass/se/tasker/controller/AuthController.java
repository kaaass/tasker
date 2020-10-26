package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.request.UserRegisterRequest;
import net.kaaass.se.tasker.controller.response.LoginResponse;
import net.kaaass.se.tasker.dto.AuthTokenDto;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.mapper.UserMapper;
import net.kaaass.se.tasker.service.AuthService;
import net.kaaass.se.tasker.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController {
    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public LoginResponse createAuthenticationToken(
            @RequestParam String username, @RequestParam String password) throws BadRequestException {
        // TODO 检查输入
        return authService.login(username, password)
                .orElseThrow(() -> new BadRequestException("用户名或密码错误！"));
    }

    @GetMapping("/refresh")
    @PreAuthorize("authenticated")
    public AuthTokenDto refreshAndGetAuthenticationToken(HttpServletRequest request) throws BadRequestException {
        String token = request.getHeader(tokenHeader);
        return authService.refresh(token)
                .orElseThrow(() -> new BadRequestException("该Token无效！"));
    }

    @PostMapping("/register")
    public UserVo register(@RequestBody UserRegisterRequest addedUser) throws BadRequestException {
        // TODO 检查输入
        return authService.register(addedUser)
                .map(userMapper::userAuthDtoToVo)
                .orElseThrow(() -> new BadRequestException("该用户名已被注册！"));
    }

    @DeleteMapping("/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable String uid) throws NotFoundException {
        authService.remove(uid);
    }
}
