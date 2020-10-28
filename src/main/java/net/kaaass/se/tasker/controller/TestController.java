package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.TaskerApplication;
import net.kaaass.se.tasker.event.TestEvent;
import net.kaaass.se.tasker.security.Role;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 */
@RestController
@RequestMapping("/test")
@Secured({Role.ADMIN})
public class TestController {


    @GetMapping("/ws")
    public void testWebsocket(@RequestParam String message) {
        TaskerApplication.EVENT_BUS.post(new TestEvent(message));
    }
}
