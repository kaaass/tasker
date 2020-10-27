package net.kaaass.se.tasker.exception.concrete;

import net.kaaass.se.tasker.exception.NotFoundException;

/**
 * 账户信息未找到错误
 */
public class ProjectNotFoundException extends NotFoundException {

    public ProjectNotFoundException() {
        this("未找到对应项目的信息，请先注册！");
    }

    public ProjectNotFoundException(String message) {
        super(message);
    }
}
