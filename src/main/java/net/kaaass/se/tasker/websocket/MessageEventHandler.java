package net.kaaass.se.tasker.websocket;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.TaskerApplication;
import net.kaaass.se.tasker.event.TaskStartEvent;
import net.kaaass.se.tasker.event.TestEvent;
import net.kaaass.se.tasker.eventhandle.SubscribeEvent;
import net.kaaass.se.tasker.mapper.TaskMapper;
import net.kaaass.se.tasker.util.Constants;
import net.kaaass.se.tasker.websocket.message.TaskStartMessage;
import org.springframework.stereotype.Component;

/**
 * WebSocket 消息处理器
 */
@Slf4j
@Component
public class MessageEventHandler {

    private final SocketIOServer server;

    private final TaskMapper taskMapper;

    public MessageEventHandler(SocketIOServer server, TaskMapper taskMapper) {
        this.server = server;
        this.taskMapper = taskMapper;
        // 注册监听器
        TaskerApplication.EVENT_BUS.register(this);
    }

    /**
     * 客户端上线
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        client.joinRoom(Constants.WS_DEFAULT_ROOM);
        log.info("客户端上线：{}", client.getRemoteAddress());
    }

    /**
     * 客户端断开
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        log.info("客户端断开连接：{}", client.getRemoteAddress());
        client.disconnect();
    }

    /**
     * 广播消息
     */
    public void broadcast(String name, Object data) {
        server.getRoomOperations(Constants.WS_DEFAULT_ROOM).sendEvent(name, data);
    }

    /**
     * 测试消息
     */
    @SubscribeEvent
    public void handleTest(TestEvent event) {
        broadcast(MessageConstants.TEST, event);
    }

    /**
     * 任务开始消息
     */
    @SubscribeEvent
    public void handleTaskStart(TaskStartEvent event) {
        var tasks = event.getStarts();
        for (var taskDto : tasks) {
            var message = new TaskStartMessage();
            message.setTask(taskMapper.dtoToVo(taskDto));
            message.setUndertakerEid(message.getTask().getUndertaker().getId());
            broadcast(MessageConstants.TASK_START, message);
        }
    }
}
