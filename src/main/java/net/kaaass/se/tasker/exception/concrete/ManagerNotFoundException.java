package net.kaaass.se.tasker.exception.concrete;

import net.kaaass.se.tasker.exception.NotFoundException;

/**
 * 经理信息未找到错误
 */
public class ManagerNotFoundException extends NotFoundException {

    public ManagerNotFoundException() {
        this("未找到对应账户的经理信息！");
    }

    public ManagerNotFoundException(String message) {
        super(message);
    }
}
