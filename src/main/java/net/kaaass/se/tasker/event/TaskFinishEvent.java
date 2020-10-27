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
 * TODO 完成下一个任务的通知和自动开始、若所有完成自动INACTIVE项目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskFinishEvent extends Event {

    TaskDto taskDto;
}
