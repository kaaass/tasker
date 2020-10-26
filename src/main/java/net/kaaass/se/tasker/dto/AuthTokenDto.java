package net.kaaass.se.tasker.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.kaaass.se.tasker.util.DateToLongSerializer;

import java.util.Date;

/**
 * 鉴权令牌数据传送对象
 */
@Data
@AllArgsConstructor
public class AuthTokenDto {
    String token;
    @JsonSerialize(using = DateToLongSerializer.class)
    Date expired;
}
