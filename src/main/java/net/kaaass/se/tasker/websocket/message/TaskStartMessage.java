package net.kaaass.se.tasker.websocket.message;

import lombok.Data;
import net.kaaass.se.tasker.vo.EmployeeVo;
import net.kaaass.se.tasker.vo.TaskVo;

/**
 * 任务开始消息
 */
@Data
public class TaskStartMessage {

    private TaskVo task;

    private String undertakerEid;
}
