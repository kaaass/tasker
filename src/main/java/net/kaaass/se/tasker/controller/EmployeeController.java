package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.request.EmployeeRequest;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.mapper.EmployeeMapper;
import net.kaaass.se.tasker.mapper.TaskMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.EmployeeService;
import net.kaaass.se.tasker.service.TaskService;
import net.kaaass.se.tasker.vo.DelegateVo;
import net.kaaass.se.tasker.vo.EmployeeVo;
import net.kaaass.se.tasker.vo.TaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 员工操作相关接口
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController extends BaseController {

    @Autowired
    private EmployeeService service;

    @Autowired
    private EmployeeMapper mapper;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMapper taskMapper;

    /**
     * 显示所有员工信息
     */
    @GetMapping("/list")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public List<EmployeeVo> listEmployee(Pageable pageable) {
        return service.getAllEmployee(pageable).stream()
                .map(mapper::dtoToVo)
                .collect(Collectors.toList());
    }

    /**
     * 更新员工信息
     */
    @PostMapping("/{eid}/update")
    @Secured({Role.ADMIN, Role.EMPLOYEE})
    public EmployeeVo updateEmployee(
            @PathVariable String eid,
            @RequestBody EmployeeRequest request) throws BadRequestException, NotFoundException {
        // FIXME 员工类型改变，对应的任务是不是也要改变？
        return mapper.dtoToVo(service.update(eid, request));
    }

    /**
     * 查看已委托给他人的任务
     */
    @GetMapping("/{eid}/delegate")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public List<DelegateVo> listDelegate(@PathVariable String eid) throws EmployeeNotFoundException {
        return taskService.listDelegateForEmployee(eid).stream()
                .map(taskMapper::dtoToVo)
                .collect(Collectors.toList());
    }

    /**
     * 获取员工被分配的任务
     */
    @GetMapping("/{eid}/task")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public List<TaskVo> listTask(@PathVariable String eid) throws EmployeeNotFoundException {
        return taskService.listTaskForEmployee(eid).stream()
                .map(taskMapper::dtoToVo)
                .collect(Collectors.toList());
    }

    /**
     * 获取当前账户员工被分配的任务
     */
    @GetMapping("/task")
    @Secured({Role.EMPLOYEE})
    public List<TaskVo> listTask() throws EmployeeNotFoundException {
        var employee = service.getByUid(getUid())
                .orElseThrow(EmployeeNotFoundException::new);
        return taskService.listTaskForEmployee(employee.getId()).stream()
                .map(taskMapper::dtoToVo)
                .collect(Collectors.toList());
    }

    /**
     * 管理员增加 Employee
     */
    @PostMapping("/add")
    @Secured({Role.ADMIN})
    public EmployeeVo addEmployee(@RequestBody EmployeeRequest request) throws BadRequestException, NotFoundException {
        if (request.getUid() == null)
            throw new BadRequestException("必须提供员工uid");
        return mapper.dtoToVo(service.add(request));
    }

    /**
     * 获得登录账户的员工信息
     */
    @GetMapping("/info")
    @Secured({Role.EMPLOYEE})
    public EmployeeVo info() throws NotFoundException {
        return service.getByUid(getUid())
                .map(mapper::dtoToVo)
                .orElseThrow(() -> new NotFoundException("当前用户的员工信息不存在！"));
    }

    /**
     * 管理员删除 Employee
     */
    @DeleteMapping("/{eid}")
    @Secured({Role.ADMIN})
    public void deleteEmployee(@PathVariable String eid) throws EmployeeNotFoundException {
        service.deleteEmployee(eid);
    }
}
