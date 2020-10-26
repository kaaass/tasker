package net.kaaass.se.tasker.dto;

import lombok.Data;

/**
 * 员工数据传输对象
 */
@Data
public class EmployeeDto {

    private String id;

    private String name;

    private EmployeeType type;

    private UserAuthDto user;

    private ManagerDto manager;
}
