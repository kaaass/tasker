package net.kaaass.se.tasker.controller.request;

import lombok.Data;
import net.kaaass.se.tasker.dao.entity.EmployeeType;
import org.springframework.lang.NonNull;

/**
 * 增加、修改员工信息请求对象
 */
@Data
public class EmployeeRequest {

    @NonNull
    String name;

    @NonNull
    EmployeeType type;
}
