package net.kaaass.se.tasker.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.eventhandle.Event;

/**
 * 用户登录事件
 *
 * 值修改将会被忽略
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserLoginEvent extends Event {

    UserDto user;
}
