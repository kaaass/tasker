package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.mapper.UserMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.UserService;
import net.kaaass.se.tasker.vo.EmployeeVo;
import net.kaaass.se.tasker.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户操作相关接口
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService service;

    @Autowired
    private UserMapper mapper;

    /**
     * 用户更新自身用户信息，或管理员更新指定用户信息
     */
    @PostMapping("/{uid}/update")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public UserVo updateUser(@PathVariable String uid) {
        // TODO
        return null;
    }

    /**
     * 管理员获取用户列表
     */
    @GetMapping("/list")
    @Secured({Role.ADMIN})
    public List<UserVo> listUser() {
        // TODO
        return null;
    }

    /**
     * 用户查看个人信息
     */
    @GetMapping("/info")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public UserVo info() {
        // TODO
        return null;
    }

    /**
     * 管理员删除员工
     */
    @DeleteMapping("/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable String uid) throws NotFoundException, BadRequestException {
        if (uid.equals(getUid()))
            throw new BadRequestException("不能删除当前账户！");
        service.remove(uid);
    }
}
