package net.kaaass.se.tasker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// FIXME: 删除本类
@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

    @GetMapping("/echo")
    public String echo(@RequestParam String input) {
        return input;
    }
}
