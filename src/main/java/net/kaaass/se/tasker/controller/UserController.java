package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.request.UserRequest;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.UserNotFoundException;
import net.kaaass.se.tasker.mapper.UserMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.UserService;
import net.kaaass.se.tasker.vo.EmployeeVo;
import net.kaaass.se.tasker.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    public UserVo updateUser(@PathVariable String uid, @RequestBody UserRequest request) throws UserNotFoundException {
        return mapper.dtoToVo(service.update(uid, request));
    }

    /**
     * 管理员获取用户列表
     */
    @GetMapping("/list")
    @Secured({Role.ADMIN})
    public List<UserVo> listUser(Pageable pageable) {
        return service.getAll(pageable).stream()
                .map(mapper::dtoToVo)
                .collect(Collectors.toList());
    }

    /**
     * 用户查看个人信息
     */
    @GetMapping("/info")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public UserVo info() throws UserNotFoundException {
        return service.getByUid(getUid())
                .map(mapper::dtoToVo)
                .orElseThrow(UserNotFoundException::new);
    }

    /**
     * 管理员添加员工
     */
    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN')")
    public UserVo addUser(@RequestBody UserRequest request) throws BadRequestException {
        return service.add(request)
                .map(mapper::dtoToVo)
                .orElseThrow(() -> new BadRequestException("该用户名已被注册！"));
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
