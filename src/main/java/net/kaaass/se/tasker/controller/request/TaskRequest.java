package net.kaaass.se.tasker.controller.request;

import lombok.Data;

import java.util.List;

/**
 * 增加、修改任务信息请求对象
 */
@Data
public class TaskRequest {

    private List<String> previousId;

    private String name;

    private String type;

    private String undertakerEid;

    private String status;

    private String projectId;
}
