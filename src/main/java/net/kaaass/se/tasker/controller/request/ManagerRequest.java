package net.kaaass.se.tasker.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 增加、修改经理信息请求对象
 */
@Data
@AllArgsConstructor
public class ManagerRequest {

    @NonNull
    String name;

    @Nullable
    String uid;
}
