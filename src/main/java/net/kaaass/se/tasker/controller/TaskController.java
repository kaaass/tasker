package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.request.DelegateRequest;
import net.kaaass.se.tasker.controller.request.TaskRequest;
import net.kaaass.se.tasker.controller.response.StatResponse;
import net.kaaass.se.tasker.exception.BadRequestException;
import net.kaaass.se.tasker.exception.ForbiddenException;
import net.kaaass.se.tasker.exception.NotFoundException;
import net.kaaass.se.tasker.exception.concrete.EmployeeNotFoundException;
import net.kaaass.se.tasker.exception.concrete.ProjectNotFoundException;
import net.kaaass.se.tasker.exception.concrete.TaskNotFoundException;
import net.kaaass.se.tasker.mapper.ResourceMapper;
import net.kaaass.se.tasker.mapper.TaskMapper;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.service.TaskService;
import net.kaaass.se.tasker.vo.DelegateVo;
import net.kaaass.se.tasker.vo.ResourceVo;
import net.kaaass.se.tasker.vo.TaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务操作相关接口
 */
@RestController
@RequestMapping("/task")
public class TaskController extends BaseController {

    @Autowired
    private TaskService service;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private ResourceMapper resourceMapper;

    /**
     * 增加任务
     */
    @PostMapping("/add")
    @Secured({Role.ADMIN, Role.MANAGER})
    public TaskVo addTask(@RequestBody TaskRequest request) throws ProjectNotFoundException, EmployeeNotFoundException, BadRequestException {
        return mapper.dtoToVo(service.add(request));
    }

    /**
     * 更新任务信息
     */
    @PostMapping("/{tid}/update")
    @Secured({Role.ADMIN, Role.MANAGER})
    public TaskVo updateTask(@PathVariable String tid,
                             @RequestBody TaskRequest request) throws TaskNotFoundException, BadRequestException, EmployeeNotFoundException, ProjectNotFoundException {
        // FIXME 任务类型改变，分配对象也要随机改变嘛？
        return mapper.dtoToVo(service.update(tid, request));
    }

    /**
     * 批量更新任务信息
     */
    @PostMapping("/update")
    @Secured({Role.ADMIN, Role.MANAGER})
    public List<TaskVo> updateTaskBatch(@RequestBody List<TaskRequest> requests) throws TaskNotFoundException, BadRequestException, EmployeeNotFoundException, ProjectNotFoundException {
        var tasks = new ArrayList<TaskVo>();
        for (var request : requests) {
            tasks.add(mapper.dtoToVo(service.update(request.getId(), request)));
        }
        return tasks;
    }

    /**
     * 获取任务信息
     */
    @GetMapping("/{tid}/info")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public TaskVo taskInfo(@PathVariable String tid) throws TaskNotFoundException, ForbiddenException, EmployeeNotFoundException {
        service.checkViewPermit(tid, getUserDto());
        return service.getById(tid).map(mapper::dtoToVo)
                .orElseThrow(TaskNotFoundException::new);
    }

    /**
     * 任务成果送审
     */
    @PostMapping("/{tid}/commit")
    @Secured({Role.EMPLOYEE})
    public TaskVo commitTask(@PathVariable String tid,
                             @RequestParam String documentId) throws NotFoundException, BadRequestException, ForbiddenException {
        service.checkViewPermit(tid, getUserDto());
        return mapper.dtoToVo(service.commitTask(tid, documentId));
    }

    /**
     * 任务完成，进入提交状态
     *
     * 同一时间一个项目只可以有一个项目进入提交状态
     *
     * @return Word 文档资源
     */
    @PostMapping("/{tid}/finish")
    @Secured({Role.EMPLOYEE})
    public ResourceVo finishTask(@PathVariable String tid) throws BadRequestException, NotFoundException, ForbiddenException {
        service.checkViewPermit(tid, getUserDto());
        return resourceMapper.dtoToVo(service.finishTask(tid));
    }

    /**
     * 经理通过任务审核，任务结束
     */
    @PostMapping("/{tid}/confirm")
    @Secured({Role.MANAGER})
    public TaskVo confirmTask(@PathVariable String tid) throws BadRequestException, TaskNotFoundException, ProjectNotFoundException, ForbiddenException, EmployeeNotFoundException {
        service.checkViewPermit(tid, getUserDto());
        return mapper.dtoToVo(service.confirmTask(tid));
    }

    /**
     * 经理拒绝任务提交，打回重做
     */
    @PostMapping("/{tid}/reject")
    @Secured({Role.MANAGER})
    public TaskVo rejectTask(@PathVariable String tid) throws BadRequestException, TaskNotFoundException, ForbiddenException, EmployeeNotFoundException {
        service.checkViewPermit(tid, getUserDto());
        return mapper.dtoToVo(service.rejectTask(tid));
    }

    /**
     * 员工将分配给自己的任务委派给他人
     */
    @PostMapping("/{tid}/delegate")
    @Secured({Role.EMPLOYEE})
    public DelegateVo delegateTask(@PathVariable String tid,
                                   @RequestBody DelegateRequest request) throws EmployeeNotFoundException, TaskNotFoundException, ForbiddenException, BadRequestException {
        service.checkViewPermit(tid, getUserDto());
        return mapper.dtoToVo(service.addDelegate(tid, request, getUid()));
    }

    /**
     * 员工收回委托
     */
    @PostMapping("/{tid}/withdraw")
    @Secured({Role.EMPLOYEE})
    public TaskVo withdrawDelegate(@PathVariable String tid) throws NotFoundException, ForbiddenException {
        service.checkViewPermit(tid, getUserDto());
        return mapper.dtoToVo(service.withdrawDelegate(tid));
    }

    @GetMapping("/stat")
    public StatResponse stat() {
        return service.stat();
    }
}
