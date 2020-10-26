package net.kaaass.se.tasker.controller.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * 用户注册请求对象
 */
@Data
public class UserRegisterRequest {

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private String name;
}
