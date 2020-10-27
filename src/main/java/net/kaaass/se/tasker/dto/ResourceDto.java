package net.kaaass.se.tasker.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.se.tasker.util.DateToLongSerializer;

import java.util.Date;

/**
 * 文件资源数据传输对象
 */
@Data
public class ResourceDto {
    private String id;

    private ResourceType type;

    private String url;

    private UserDto uploader;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date uploadTime;
}
