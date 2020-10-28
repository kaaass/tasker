package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.request.ManagerRequest;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ManagerNotFoundException;
import net.kaaass.se.tasker.exception.concrete.UserNotFoundException;
import net.kaaass.se.tasker.mapper.EmployeeMapper;
import net.kaaass.se.tasker.mapper.ManagerMapper;
import net.kaaass.se.tasker.mapper.TaskMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.ManagerService;
import net.kaaass.se.tasker.vo.EmployeeVo;
import net.kaaass.se.tasker.vo.ManagerVo;
import net.kaaass.se.tasker.vo.TaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 经理操作相关接口
 */
@RestController
@RequestMapping("/manager")
public class ManagerController extends BaseController {

    @Autowired
    private ManagerService service;

    @Autowired
    private ManagerMapper mapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private TaskMapper taskMapper;

    /**
     * 向经理组内增加员工
     */
    @PostMapping("/{mid}/employee/add")
    @Secured({Role.ADMIN, Role.MANAGER})
    public EmployeeVo addEmployee(@PathVariable String mid,
                                  @RequestParam String eid) throws EmployeeNotFoundException, ManagerNotFoundException {
        return service.addToGroup(mid, eid)
                .map(employeeMapper::dtoToVo)
                .orElseThrow();
    }

    /**
     * 从经理组内移除员工
     */
    @PostMapping("/{mid}/employee/remove")
    @Secured({Role.ADMIN, Role.MANAGER})
    public void removeEmployee(@PathVariable String mid,
                               @RequestParam String eid) throws EmployeeNotFoundException, ManagerNotFoundException {
        service.deleteFromGroup(mid, eid);
    }

    /**
     * 显示所有经理
     */
    @GetMapping("/list")
    @Secured({Role.ADMIN})
    public List<ManagerVo> listManager(Pageable pageable) {
        return service.getAll(pageable).stream()
                .filter(managerDto -> !managerDto.isDeleted())
                .map(mapper::dtoToVo)
                .collect(Collectors.toList());
    }

    /**
     * 显示经理组内的所有员工
     */
    @GetMapping("/{mid}/employee/list")
    @Secured({Role.ADMIN, Role.MANAGER})
    public List<EmployeeVo> listEmployeeOfManager(@PathVariable String mid) {
        return service.getGroupMember(mid).stream()
                .map(employeeMapper::dtoToVo)
                .collect(Collectors.toList());
    }

    /**
     * 更新经理信息
     */
    @PostMapping("/{mid}/update")
    @Secured({Role.ADMIN, Role.MANAGER})
    public ManagerVo updateManager(
            @PathVariable String mid,
            @RequestBody ManagerRequest request) throws ManagerNotFoundException {
        return mapper.dtoToVo(service.update(mid, request));
    }

    /**
     * 管理员增加经理
     */
    @PostMapping("/add")
    @Secured({Role.ADMIN})
    public ManagerVo addManager(@RequestBody ManagerRequest request) throws BadRequestException, UserNotFoundException {
        if (request.getUid() == null)
            throw new BadRequestException("必须提供经理uid");
        return mapper.dtoToVo(service.add(request));
    }

    /**
     * 获得登录账户的经理信息
     */
    @GetMapping("/info")
    @Secured({Role.MANAGER})
    public ManagerVo info() throws ManagerNotFoundException {
        return service.getByUid(getUid())
                .map(mapper::dtoToVo)
                .orElseThrow(ManagerNotFoundException::new);
    }

    @GetMapping("/{mid}/task")
    @Secured({Role.ADMIN, Role.MANAGER})
    List<TaskVo> listTask(@PathVariable String mid) throws ManagerNotFoundException {
        return service.listTaskForManager(mid).stream()
                .map(taskMapper::dtoToVo)
                .collect(Collectors.toList());
    }

    @GetMapping("/task")
    @Secured({Role.MANAGER})
    List<TaskVo> listTask() throws ManagerNotFoundException {
        var manager = service.getByUid(getUid()).orElseThrow(ManagerNotFoundException::new);
        return service.listTaskForManager(manager.getId()).stream()
                .map(taskMapper::dtoToVo)
                .collect(Collectors.toList());
    }

    /**
     * 管理员删除经理
     */
    @DeleteMapping("/{mid}")
    @Secured({Role.ADMIN})
    public void deleteManager(@PathVariable String mid) throws ManagerNotFoundException {
        service.deleteManager(mid);
    }
}
