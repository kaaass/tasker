package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.request.GenerateProjectRequest;
import net.kaaass.se.tasker.controller.response.ProjectInfoResponse;
import net.kaaass.se.tasker.dto.TaskType;
import net.kaaass.se.tasker.exception.ForbiddenException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ManagerNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ProjectNotFoundException;
import net.kaaass.se.tasker.mapper.ProjectMapper;
import net.kaaass.se.tasker.mapper.TaskMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.EmployeeService;
import net.kaaass.se.tasker.service.ManagerService;
import net.kaaass.se.tasker.service.ProjectService;
import net.kaaass.se.tasker.vo.ProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 项目操作相关接口
 */
@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Autowired
    private ProjectService service;

    @Autowired
    private ProjectMapper mapper;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private TaskMapper taskMapper;

    /**
     * 根据权限显示所有项目
     *
     * 管理员：所有；
     * 经理：管理的项目；
     * 员工：被分配的项目
     */
    @GetMapping("/list")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public List<ProjectVo> listProject(Pageable pageable) throws ManagerNotFoundException, EmployeeNotFoundException {
        var user = getUserDto();
        if (user.getRoles().contains(Role.ADMIN)) {
            // 管理员查看所有项目
            return service.getAll(pageable).stream()
                    .map(mapper::dtoToVo)
                    .collect(Collectors.toList());
        } else if (user.getRoles().contains(Role.MANAGER)) {
            // 经理查看管理的项目
            var manager = managerService.getByUid(getUid())
                    .orElseThrow(ManagerNotFoundException::new);
            return service.getAllForManager(manager, pageable).stream()
                    .map(mapper::dtoToVo)
                    .collect(Collectors.toList());
        } else if (user.getRoles().contains(Role.EMPLOYEE)) {
            // 员工查看被分配的项目
            var employee = employeeService.getByUid(getUid())
                    .orElseThrow(EmployeeNotFoundException::new);
            return service.getAllForEmployee(employee, pageable).stream()
                    .map(mapper::dtoToVo)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * 开始项目
     *
     * 若是新项目则开始，若是暂停项目则重启
     * 对于管理员，只有自身的项目才可以开始
     */
    @PostMapping("/{pid}/start")
    @Secured({Role.ADMIN, Role.MANAGER})
    public ProjectVo startProject(@PathVariable String pid) throws ProjectNotFoundException {
        return mapper.dtoToVo(service.startProject(pid));
    }

    /**
     * 暂停项目的执行
     *
     * 对于管理员，只有自身的项目才可以暂停
     */
    @PostMapping("/{pid}/stop")
    @Secured({Role.ADMIN, Role.MANAGER})
    public ProjectVo stopProject(@PathVariable String pid) throws ProjectNotFoundException {
        return mapper.dtoToVo(service.stopProject(pid));
    }

    /**
     * 生成一个归属于请求者的项目
     */
    @PostMapping("/generate")
    @Secured({Role.MANAGER})
    public ProjectVo generateProject(GenerateProjectRequest request) {
        int randCount = 0;
        int rest = request.getTotal();
        var taskCounts = request.getTaskCounts();
        // 统计剩余数量
        for (var type : TaskType.values()) {
            if (!taskCounts.containsKey(type)) {
                taskCounts.put(type, null);
            }
            if (taskCounts.get(type) == null) {
                randCount++;
                continue;
            }
            rest -= taskCounts.get(type);
        }
        if (rest < 0)
            rest = 0;
        // 随机分配
        var rand = new Random();
        for (var type : TaskType.values()) {
            if (taskCounts.get(type) != null) {
                continue;
            }
            int alloc;
            if (--randCount == 0) {
                alloc = rest;
            } else {
                alloc = rand.nextInt(rest + 1);
                rest -= alloc;
            }
            taskCounts.put(type, alloc);
        }
        // 如果还有剩余，则是全部确定的但是总数更大
        request.setTotal(taskCounts.values().stream()
                .mapToInt(Integer::intValue)
                .sum());
        return service.generateProject(request);
    }

    /**
     * 查询项目详细信息
     */
    @GetMapping("/{pid}")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public ProjectInfoResponse projectInfo(@PathVariable String pid)
            throws ProjectNotFoundException, ForbiddenException, ManagerNotFoundException, EmployeeNotFoundException {
        // 检查查看权限
        service.checkViewPermit(pid, getUserDto());
        // 查询
        var response = new ProjectInfoResponse();
        response.setInfo(service.getByPid(pid)
                .map(mapper::dtoToVo)
                .orElseThrow(ProjectNotFoundException::new));
        response.setTasks(service.getProjectTasks(pid).stream()
                .map(taskMapper::dtoToVo)
                .collect(Collectors.toList()));
        return response;
    }
}
