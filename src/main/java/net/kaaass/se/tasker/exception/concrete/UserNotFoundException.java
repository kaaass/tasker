package net.kaaass.se.tasker.exception.concrete;

import net.kaaass.se.tasker.exception.NotFoundException;

/**
 * 账户信息未找到错误
 */
public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        this("未找到对应账户的信息，请先注册！");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
