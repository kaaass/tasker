package net.kaaass.se.tasker.controller.request;

import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 增加、修改任务信息请求对象
 */
@Data
public class TaskRequest {

    @Nullable
    private String id;

    @NonNull
    private List<String> previousId;

    @NonNull
    private String name;

    @NonNull
    private String type;

    @NonNull
    private String undertakerEid;

    @NonNull
    private String projectId;
}
