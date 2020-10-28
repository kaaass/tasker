package net.kaaass.se.tasker.vo;

import lombok.Data;

/**
 * 经理视图对象
 */
@Data
public class ManagerVo {

    private String id;

    private String name;

    private UserVo user;

    private boolean deleted;
}
