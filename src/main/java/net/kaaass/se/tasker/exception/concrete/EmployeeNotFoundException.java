package net.kaaass.se.tasker.exception.concrete;

import net.kaaass.se.tasker.exception.NotFoundException;

/**
 * 员工信息未找到错误
 *
 * TODO 将所有员工404换为这个
 */
public class EmployeeNotFoundException extends NotFoundException {

    public EmployeeNotFoundException() {
        this("未找到对应账户的员工信息！");
    }

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
