package net.kaaass.se.tasker.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.se.tasker.dto.TaskStatus;
import net.kaaass.se.tasker.dto.TaskType;
import net.kaaass.se.tasker.util.DateToLongSerializer;

import java.util.Date;
import java.util.Set;

/**
 * 任务视图对象
 */
@Data
public class TaskVo {

    private String id;

    private String name;

    private TaskType type;

    private EmployeeVo undertaker;

    private TaskStatus status;

    private ProjectVo project;

    /**
     * 待审核的项目文档
     */
    private ResourceVo pending;

    private boolean delegate;

    private Set<String> previousId;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createTime;
}
