package net.kaaass.se.tasker.controller.response;

import lombok.Data;
import net.kaaass.se.tasker.vo.ProjectVo;
import net.kaaass.se.tasker.vo.TaskVo;

import java.util.List;

/**
 * 项目详细信息响应对象
 */
@Data
public class ProjectInfoResponse {

    private ProjectVo info;

    private List<TaskVo> tasks;
}
