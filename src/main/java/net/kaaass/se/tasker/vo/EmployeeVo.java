package net.kaaass.se.tasker.vo;

import lombok.Data;
import net.kaaass.se.tasker.dto.EmployeeType;

/**
 * 员工视图对象
 */
@Data
public class EmployeeVo {

    private String id;

    private String name;

    private EmployeeType type;

    private UserVo user;

    private ManagerVo manager;
}
