package net.kaaass.se.tasker.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动 WebSocket 服务器 Runner
 */
@Slf4j
@Component
@Order(1)
public class WebSocketServerRunner implements CommandLineRunner {

    @Autowired
    private SocketIOServer server;

    @Override
    public void run(String... args) throws Exception {
        log.info("正在启动 WebSocket 服务器...");
        server.start();
    }
}
