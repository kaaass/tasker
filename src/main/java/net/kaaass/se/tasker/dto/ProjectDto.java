package net.kaaass.se.tasker.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.se.tasker.util.DateToLongSerializer;

import java.util.Date;

/**
 * 项目数据传输对象
 */
@Data
public class ProjectDto {

    private String id;

    private String name;

    private ManagerDto undertaker;

    private ProjectStatus status;

    private ResourceDto doc;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date createTime;
}
