package net.kaaass.se.tasker.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * 增加、修改用户请求对象
 */
@Data
@NoArgsConstructor
public class UserRequest {

    @NonNull
    private String username;

    @NonNull
    private String password;
}
