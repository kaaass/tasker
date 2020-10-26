package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.request.DelegateRequest;
import net.kaaass.se.tasker.controller.request.TaskRequest;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.vo.DelegateVo;
import net.kaaass.se.tasker.vo.ResourceVo;
import net.kaaass.se.tasker.vo.TaskVo;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务操作相关接口
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    /**
     * 员工将分配给自己的任务委派给他人
     */
    @PostMapping("/{tid}/delegate")
    @Secured({Role.EMPLOYEE})
    public DelegateVo delegateTask(@PathVariable String tid,
                                   @RequestBody DelegateRequest request) {
        // TODO
        return null;
    }

    /**
     * 更新任务信息
     */
    @PostMapping("/{tid}/update")
    @Secured({Role.ADMIN, Role.MANAGER})
    public TaskVo updateTask(@PathVariable String tid,
                             @RequestBody TaskRequest request) {
        // TODO
        // 任务类型改变，分配对象也要随机改变嘛？
        return null;
    }

    /**
     * 批量更新任务信息
     */
    @PostMapping("/update")
    @Secured({Role.ADMIN, Role.MANAGER})
    public List<TaskVo> updateTaskBatch(@RequestBody List<TaskRequest> request) {
        // TODO
        return null;
    }

    /**
     * 增加任务
     */
    @PostMapping("/add")
    @Secured({Role.ADMIN, Role.MANAGER})
    public TaskVo addTask(@RequestBody TaskRequest request) {
        // TODO
        return null;
    }

    /**
     * 获取任务信息
     */
    @GetMapping("/{tid}/info")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public TaskVo taskInfo(@PathVariable String tid) {
        // TODO
        return null;
    }

    /**
     * 任务成果送审
     */
    @PostMapping("/{tid}/commit")
    @Secured({Role.EMPLOYEE})
    public TaskVo commitTask(@PathVariable String tid,
                             @RequestParam String documentId) {
        // TODO
        return null;
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
    public ResourceVo finishTask(@PathVariable String tid) {
        // TODO
        return null;
    }

    /**
     * 经理通过任务审核，任务结束
     */
    @PostMapping("/{tid}/confirm")
    @Secured({Role.MANAGER})
    public TaskVo confirmTask(@PathVariable String tid) {
        // TODO
        return null;
    }

    /**
     * 经理拒绝任务提交，打回重做
     */
    @PostMapping("/{tid}/reject")
    @Secured({Role.MANAGER})
    public TaskVo rejectTask(@PathVariable String tid) {
        // TODO
        return null;
    }

    /**
     * 员工收回委托
     */
    @PostMapping("/{tid}/withdraw")
    @Secured({Role.EMPLOYEE})
    public TaskVo withdrawTask(@PathVariable String tid) {
        // TODO
        return null;
    }
}
