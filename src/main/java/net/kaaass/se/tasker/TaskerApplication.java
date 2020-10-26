package net.kaaass.se.tasker;

import net.kaaass.se.tasker.eventhandle.EventBus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskerApplication {

    public static final EventBus EVENT_BUS = new EventBus();

    public static void main(String[] args) {
        SpringApplication.run(TaskerApplication.class, args);
    }

}
