package net.kaaass.se.tasker.controller;

import net.kaaass.se.tasker.controller.response.GlobalResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

// FIXME: 删除本类
@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

    @GetMapping("/echo")
    public GlobalResponse<String> echo(@RequestParam String input) {
        return GlobalResponse.success(input);
    }

    @GetMapping("/empty")
    public void empty() {

    }

    @GetMapping("/list")
    public List<String> list() {
        return new ArrayList<>() {{
            add("test");
            add("java");
        }};
    }
}
