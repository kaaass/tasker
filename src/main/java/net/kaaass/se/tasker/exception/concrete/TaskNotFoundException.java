package net.kaaass.se.tasker.exception.concrete;

import net.kaaass.se.tasker.exception.NotFoundException;

/**
 * 任务信息未找到错误
 */
public class TaskNotFoundException extends NotFoundException {

    public TaskNotFoundException() {
        this("未找到对应任务的信息，请先注册！");
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}
