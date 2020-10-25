package net.kaaass.se.tasker.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kaaass.se.tasker.dto.AuthTokenDto;

import java.util.List;

/**
 * 登录接口响应对象
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    private AuthTokenDto authToken;

    private String username;

    private List<String> roles;
}
