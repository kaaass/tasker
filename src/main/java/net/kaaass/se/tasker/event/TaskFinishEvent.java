package net.kaaass.se.tasker.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kaaass.se.tasker.dto.TaskDto;
import net.kaaass.se.tasker.eventhandle.Event;

/**
 * 任务完成事件
 *
 * 值修改将会被忽略
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskFinishEvent extends Event {

    TaskDto taskDto;
}
