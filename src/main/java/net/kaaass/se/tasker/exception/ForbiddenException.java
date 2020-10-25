package net.kaaass.se.tasker.exception;

import lombok.Getter;
import net.kaaass.se.tasker.util.StatusEnum;

/**
 * 无权请求错误
 */
@Getter
public class ForbiddenException extends BaseException {
    StatusEnum status = StatusEnum.FORBIDDEN;

    public ForbiddenException(String message) {
        super(message);
    }
}