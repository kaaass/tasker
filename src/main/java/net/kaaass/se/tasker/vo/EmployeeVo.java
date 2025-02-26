package net.kaaass.se.tasker.vo;

import lombok.Data;
import net.kaaass.se.tasker.dto.TaskType;

/**
 * 员工视图对象
 */
@Data
public class EmployeeVo {

    private String id;

    private String name;

    private TaskType type;

    private UserVo user;

    private ManagerVo manager;

    private boolean deleted;

    public String getName() {
        if (deleted)
            return "用户已删除";
        return this.name;
    }
}
