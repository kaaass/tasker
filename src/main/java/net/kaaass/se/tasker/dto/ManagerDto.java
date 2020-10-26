package net.kaaass.se.tasker.dto;

import lombok.Data;

/**
 * 经理数据传输对象
 */
@Data
public class ManagerDto {

    private String id;

    private String name;

    private UserAuthDto user;
}
