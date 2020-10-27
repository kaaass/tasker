package net.kaaass.se.tasker.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.se.tasker.dto.EmployeeType;
import net.kaaass.se.tasker.dto.TaskStatus;
import net.kaaass.se.tasker.util.DateToLongSerializer;

import java.util.Date;

/**
 * 任务视图对象
 */
@Data
public class TaskVo {

    private String id;

    private String name;

    private EmployeeType type;

    private EmployeeVo undertaker;

    private TaskStatus status;

    private ProjectVo project;

    /**
     * 待审核的项目文档
     */
    private ResourceVo pendding;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createTime;
}
