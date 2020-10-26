package net.kaaass.se.tasker.util;

import org.springframework.util.ClassUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class StringUtils {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String resourcePath() {
        return Objects.requireNonNull(Objects.requireNonNull(ClassUtils.getDefaultClassLoader()).getResource("")).getPath();
    }
}
