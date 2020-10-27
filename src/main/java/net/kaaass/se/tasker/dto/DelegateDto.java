package net.kaaass.se.tasker.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.se.tasker.util.DateToLongSerializer;

import java.util.Date;

/**
 * 委托数据传输对象
 */
@Data
public class DelegateDto {

    private String id;

    private TaskDto task;

    private EmployeeDto from;

    private EmployeeDto delegateTo;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date expire;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createTime;
}
