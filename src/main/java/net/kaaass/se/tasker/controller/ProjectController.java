package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.response.ProjectInfoResponse;
import net.kaaass.se.tasker.security.Role;
import net.kaaass.se.tasker.vo.ManagerVo;
import net.kaaass.se.tasker.vo.ProjectVo;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目操作相关接口
 */
@RestController
@RequestMapping("/project")
public class ProjectController {

    /**
     * 根据权限显示所有项目
     *
     * 超管：所有；
     * 经理：管理的项目；
     * 员工：被分配的项目
     */
    @GetMapping("/list")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public List<ProjectVo> listProject() {
        // TODO
        return null;
    }

    /**
     * 开始项目
     *
     * 若是新项目则开始，若是暂停项目则重启
     * 对于管理员，只有自身的项目才可以开始
     */
    @PostMapping("/{pid}/start")
    @Secured({Role.ADMIN, Role.MANAGER})
    public ProjectVo startProject(@PathVariable String pid) {
        // TODO
        return null;
    }

    /**
     * 暂停项目的执行
     *
     * 对于管理员，只有自身的项目才可以暂停
     */
    @PostMapping("/{pid}/stop")
    @Secured({Role.ADMIN, Role.MANAGER})
    public ProjectVo stopProject(@PathVariable String pid) {
        // TODO
        return null;
    }

    /**
     * 生成一个归属于请求者的项目
     */
    @PostMapping("/generate")
    @Secured({Role.MANAGER})
    public ProjectVo generateProject() {
        // TODO
        return null;
    }

    /**
     * 查询项目详细信息
     */
    @GetMapping("/{pid}")
    @Secured({Role.ADMIN, Role.MANAGER, Role.EMPLOYEE})
    public ProjectInfoResponse projectInfo(@PathVariable String pid) {
        // TODO
        // 这里每次查询任务都 lazy 检查一下是否有过期的委托
        return null;
    }
}
