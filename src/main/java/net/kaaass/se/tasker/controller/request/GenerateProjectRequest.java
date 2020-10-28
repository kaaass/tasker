package net.kaaass.se.tasker.controller.request;

import lombok.Data;
import lombok.NonNull;
import net.kaaass.se.tasker.dto.TaskType;

import java.util.Map;

/**
 * 生成项目信息请求对象
 */
@Data
public class GenerateProjectRequest {

    @NonNull
    String name;

    /**
     * 子任务总数
     */
    @NonNull
    Integer total;

    /**
     * 不同类型子任务总数，不填或为空则随机生成
     */
    @NonNull
    Map<TaskType, Integer> taskCounts;
}
