package net.kaaass.se.tasker.controller.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import net.kaaass.se.tasker.util.LongToLocalDateTimeDeserializer;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

/**
 * 增加、修改委托信息请求对象
 */
@Data
public class DelegateRequest {

    /**
     * 委派给员工的 eid
     */
    @NonNull
    private String delegateTo;

    /**
     * 委派收回时间
     */
    @NonNull
    @JsonDeserialize(using = LongToLocalDateTimeDeserializer.class)
    private LocalDateTime expire;
}
