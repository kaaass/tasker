package net.kaaass.se.tasker.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.kaaass.se.tasker.eventhandle.Cancelable;
import net.kaaass.se.tasker.eventhandle.Event;
import org.aspectj.lang.JoinPoint;

/**
 * Controller返回前事件
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Cancelable
public class BeforeControllerEvent extends Event {

    private Object[] args;

    private JoinPoint.StaticPart staticPart;
}
