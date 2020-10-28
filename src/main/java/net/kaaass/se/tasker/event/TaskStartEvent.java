package net.kaaass.se.tasker.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kaaass.se.tasker.dto.TaskDto;
import net.kaaass.se.tasker.eventhandle.Event;

import java.util.List;

/**
 * 任务开始事件
 *
 * 值修改将会被忽略
 * TODO 完成下一个任务的通知和自动开始（前提是项目处于ACTIVE状态）
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskStartEvent extends Event {

    List<TaskDto> starts;
}
