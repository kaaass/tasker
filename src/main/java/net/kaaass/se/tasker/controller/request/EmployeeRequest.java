package net.kaaass.se.tasker.controller.request;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;

/**
 * 增加、修改员工信息请求对象
 */
@Data
@RequiredArgsConstructor
public class EmployeeRequest {

    @NonNull
    String name;

    @NonNull
    String type;

    @Nullable
    String uid;
}
