package net.kaaass.se.tasker.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.se.tasker.dto.ProjectStatus;
import net.kaaass.se.tasker.util.DateToLongSerializer;

import java.util.Date;

/**
 * 项目视图对象
 */
@Data
public class ProjectVo {

    private String id;

    private String name;

    private ManagerVo undertaker;

    private ProjectStatus status;

    private ResourceVo doc;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createTime;
}
