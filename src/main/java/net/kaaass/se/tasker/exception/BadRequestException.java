package net.kaaass.se.tasker.exception;

import lombok.Getter;
import net.kaaass.se.tasker.util.StatusEnum;

/**
 * 坏请求错误
 */
@Getter
public class BadRequestException extends BaseException {
    StatusEnum status = StatusEnum.BAD_REQUEST;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}