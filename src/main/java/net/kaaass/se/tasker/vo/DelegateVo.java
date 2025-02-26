package net.kaaass.se.tasker.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.se.tasker.util.DateToLongSerializer;

import java.util.Date;

/**
 * 委托视图对象
 */
@Data
public class DelegateVo {

    private String id;

    private TaskVo task;

    private EmployeeVo from;

    private EmployeeVo delegateTo;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date expire;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createTime;
}
