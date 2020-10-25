package net.kaaass.se.tasker.exception;

import lombok.Getter;
import net.kaaass.se.tasker.util.StatusEnum;

/**
 * 服务器暂不可用
 */
@Getter
public class ServiceUnavailableException extends BaseException {

    StatusEnum status = StatusEnum.SERVICE_UNAVAILABLE;

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
