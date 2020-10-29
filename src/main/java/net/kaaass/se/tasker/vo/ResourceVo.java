package net.kaaass.se.tasker.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import net.kaaass.se.tasker.util.DateToLongSerializer;
import net.kaaass.se.tasker.dto.ResourceType;

import java.util.Date;

/**
 * 资源对象
 */
@Data
public class ResourceVo {
    private String id;

    private ResourceType type;

    private String url;

    private String uploaderUid;

    @JsonSerialize(using = DateToLongSerializer.class)
    private Date uploadTime;
}
