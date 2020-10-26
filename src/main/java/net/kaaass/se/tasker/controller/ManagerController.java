package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.request.ManagerRequest;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.vo.EmployeeVo;
import net.kaaass.se.tasker.vo.ManagerVo;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 经理操作相关接口
 */
@RestController
@RequestMapping("/manager")
public class ManagerController {

    /**
     * 向经理组内增加员工
     */
    @PostMapping("/{mid}/employee/add")
    @Secured({Role.ADMIN, Role.MANAGER})
    public EmployeeVo addEmployee(@PathVariable String mid) {
        // TODO
        return null;
    }

    /**
     * 从经理组内移除员工
     */
    @PostMapping("/{mid}/employee/remove")
    @Secured({Role.ADMIN, Role.MANAGER})
    public void removeEmployee(@PathVariable String mid,
                               @RequestParam String eid) {
        // TODO
    }

    /**
     * 显示所有经理
     */
    @GetMapping("/list")
    @Secured({Role.ADMIN})
    public List<ManagerVo> listManager() {
        // TODO
        return null;
    }

    /**
     * 显示所有经理
     */
    @GetMapping("/{mid}/employee/list")
    @Secured({Role.ADMIN, Role.MANAGER})
    public List<EmployeeVo> listEmployeeOfManager(@PathVariable String mid) {
        // TODO
        return null;
    }

    /**
     * 更新经理信息
     */
    @PostMapping("/{mid}/update")
    @Secured({Role.ADMIN, Role.MANAGER})
    public ManagerVo updateManager(
            @PathVariable String mid,
            @RequestBody ManagerRequest request) {
        // TODO
        return null;
    }

    /**
     * 管理员增加经理
     */
    @PostMapping("/add")
    @Secured({Role.ADMIN})
    public ManagerVo addManager(@RequestBody ManagerRequest request) {
        // TODO
        return null;
    }

    /**
     * 获得登录账户的经理信息
     */
    @GetMapping("/info")
    @Secured({Role.MANAGER})
    public ManagerVo info() {
        // TODO
        return null;
    }
}
