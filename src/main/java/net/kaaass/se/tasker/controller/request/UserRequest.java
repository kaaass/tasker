package net.kaaass.se.tasker.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 增加、修改用户请求对象
 */
@Data
public class UserRequest {

    @NonNull
    private String username;

    @Nullable
    private String password;
}
