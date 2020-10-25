package net.kaaass.se.tasker.vo;

import lombok.Data;

import java.util.List;

/**
 * 用户鉴权视图对象
 */
@Data
public class UserVo {
    private String id;

    private String username;

    private List<String> roles;
}
