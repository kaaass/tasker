package net.kaaass.se.tasker.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.se.tasker.util.DateToLongSerializer;

import java.util.Date;
import java.util.Set;

/**
 * 任务数据传输对象
 */
@Data
public class TaskDto {

    private String id;

    private String name;

    private TaskType type;

    private EmployeeDto undertaker;

    private TaskStatus status;

    private ProjectDto project;

    /**
     * 待审核的项目文档
     */
    private ResourceDto pending;

    private boolean delegate;

    private Set<TaskDto> previous;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createTime;
}
