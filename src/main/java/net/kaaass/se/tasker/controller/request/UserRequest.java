package net.kaaass.se.tasker.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * 增加、修改用户请求对象
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NonNull
    private String username;

    @Nullable
    private String password;

    @Nullable
    private String avatarId;
}
