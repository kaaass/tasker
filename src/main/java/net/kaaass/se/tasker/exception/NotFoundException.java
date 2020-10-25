package net.kaaass.se.tasker.exception;

import lombok.Getter;
import net.kaaass.se.tasker.util.StatusEnum;

/**
 * 未找到错误
 */
@Getter
public class NotFoundException extends BaseException {
    StatusEnum status = StatusEnum.NOT_FOUND;

    public NotFoundException(String message) {
        super(message);
    }
}
