package net.kaaass.se.tasker.service;

import lombok.SneakyThrows;
import net.kaaass.se.tasker.controller.request.UserRegisterRequest;
import net.kaaass.se.tasker.dto.UserDto;
import net.kaaass.se.tasker.security.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAuthService {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @SneakyThrows
    @Test
    public void testRegisterAndRemove() {
        var request = new UserRegisterRequest(
                "kas",
                "123123",
                "A",
                "KAs"
        );

        UserDto result = authService.register(request).orElseThrow();

        var query = userService.getByUid(result.getId()).orElseThrow();
        assertEquals("kas", query.getUsername());
        assertNotEquals("123123", query.getPassword()); // 密码不是明文存储
        assertTrue(passwordEncoder.matches("123123", query.getPassword()));

        userService.remove(result.getId());

        var empty = userService.getByUid(result.getId());
        assertTrue(empty.isEmpty());
    }

    @SneakyThrows
    @Test
    public void testLogin() {
        var request = new UserRegisterRequest(
                "kas",
                "123123",
                "A",
                "KAs"
        );
        var user = authService.register(request).orElseThrow();
        // start
        var logged = authService.login("kas",
                "123123");
        assertTrue(logged.isPresent());
        var response = logged.get();
        assertEquals("kas", response.getUsername());
        assertEquals(List.of(Role.USER), response.getRoles());
        // end
        userService.remove(user.getId());
    }
}
