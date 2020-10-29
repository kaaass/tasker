package net.kaaass.se.tasker.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户登录数据传输对象
 */
@Data
public class UserDto {

    private String id;

    private String username;

    private String password;

    private List<String> roles;

    boolean enable = true;

    private ResourceDto avatar;

    private Date lastLoginTime;
}