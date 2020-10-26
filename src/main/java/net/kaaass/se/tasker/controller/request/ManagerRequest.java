package net.kaaass.se.tasker.controller.request;

import lombok.Data;
import org.springframework.lang.NonNull;

/**
 * 增加、修改经理信息请求对象
 */
@Data
public class ManagerRequest {

    @NonNull
    String name;
}
