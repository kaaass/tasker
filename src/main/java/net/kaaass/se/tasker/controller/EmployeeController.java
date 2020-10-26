package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.request.EmployeeRequest;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.vo.DelegateVo;
import net.kaaass.se.tasker.vo.EmployeeVo;
import net.kaaass.se.tasker.vo.TaskVo;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工操作相关接口
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController extends BaseController {

    /**
     * 显示所有员工信息
     */
    @GetMapping("/list")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public List<EmployeeVo> listEmployee() {
        // TODO
        return null;
    }

    /**
     * 更新员工信息
     */
    @PostMapping("/{eid}/update")
    @Secured({Role.ADMIN, Role.EMPLOYEE})
    public EmployeeVo updateEmployee(
            @PathVariable String eid,
            @RequestBody EmployeeRequest request) {
        // TODO
        return null;
    }

    /**
     * 查看已委托给他人的任务
     */
    @GetMapping("/{eid}/delegate")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public List<DelegateVo> listDelegate(@PathVariable String eid) {
        // TODO
        return null;
    }

    /**
     * 获取员工被分配的任务
     */
    @GetMapping("/{eid}/task")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public List<TaskVo> listTask(@PathVariable String eid) {
        // TODO
        return null;
    }

    /**
     * 管理员增加 Employee
     */
    @PostMapping("/add")
    @Secured({Role.ADMIN})
    public EmployeeVo addEmployee(@RequestBody EmployeeRequest request) {
        // TODO
        return null;
    }

    /**
     * 获得登录账户的员工信息
     */
    @GetMapping("/info")
    @Secured({Role.EMPLOYEE})
    public EmployeeVo info() {
        // TODO
        return null;
    }
}
