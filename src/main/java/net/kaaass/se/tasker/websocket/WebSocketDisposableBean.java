package net.kaaass.se.tasker.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketDisposableBean implements DisposableBean {

    @Autowired
    private SocketIOServer server;

    @Override
    public void destroy() throws Exception {
        log.info("正在停止 WebSocket 服务器...");
        server.stop();
    }
}
