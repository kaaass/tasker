package net.kaaass.se.tasker.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kaaass.se.tasker.dto.ProjectStatus;
import net.kaaass.se.tasker.dto.TaskStatus;

import java.util.Map;

/**
 * 统计接口响应对象
 */
@Data
@AllArgsConstructor
public class StatResponse {

    private Map<ProjectStatus, Double> project;

    private Map<TaskStatus, Double> task;
}
