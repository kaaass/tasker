package net.kaaass.se.tasker.controller.request;

import lombok.Data;

/**
 * 增加、修改委托信息请求对象
 */
@Data
public class DelegateRequest {

    /**
     * 委派给员工的 eid
     */
    private String delegateTo;

    /**
     * 委派收回时间
     */
    private long expire;
}
