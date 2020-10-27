package net.kaaass.se.tasker.dto;

import net.kaaass.se.tasker.exception.BadRequestException;

/**
 * 员工类别
 */
public enum TaskType {
    A, B, C;

    public static TaskType valueOfThrow(String value) throws BadRequestException {
        try {
            return valueOf(value);
        } catch (IllegalArgumentException ignore) {
            throw new BadRequestException("员工类型不存在！");
        }
    }
}
