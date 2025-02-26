package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 控制器基类
 */
public class BaseController {
    @Autowired
    private UserService userService;

    /**
     * 从请求中获得用户详细信息对象
     */
    private static UserDetails getAuthUserDetail() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        return (UserDetails) auth.getPrincipal();
    }

    /**
     * 获得鉴权用户的uid
     */
    protected static String getUid() {
        return getAuthUserDetail().getUsername();
    }

    /**
     * 获得鉴权用户的 auth dto，方法将会请求数据库
     */
    protected UserDto getUserDto() {
        return userService.getByUid(getUid()).orElseThrow();
    }
}
