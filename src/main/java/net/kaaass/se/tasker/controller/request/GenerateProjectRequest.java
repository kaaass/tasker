package net.kaaass.se.tasker.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kaaass.se.tasker.dto.TaskType;

import java.util.Map;

/**
 * 生成项目信息请求对象
 */
@Data
@NoArgsConstructor
public class GenerateProjectRequest {

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
