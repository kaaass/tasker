package net.kaaass.se.tasker.exception;

import lombok.Getter;
import net.kaaass.se.tasker.util.StatusEnum;

/**
 * 通用错误类
 *
 * 记录状态码
 */
@Getter
public class BaseException extends Exception {
    StatusEnum status = StatusEnum.SUCCESS;

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
