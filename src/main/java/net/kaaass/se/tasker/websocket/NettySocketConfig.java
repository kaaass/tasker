package net.kaaass.se.tasker.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.extern.slf4j.Slf4j;
import net.kaaass.se.tasker.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WebSocket 配置
 */
@Slf4j
@Configuration
public class NettySocketConfig {

    @Value("${websocket.hostname}")
    private String hostname;

    @Value("${websocket.port}")
    private int port;

    @Value("${websocket.upgrade-timeout}")
    private int upgradeTimeout;

    @Value("${websocket.ping-interval}")
    private int pingInterval;

    @Value("${websocket.ping-timeout}")
    private int pingTimeout;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 创建 Socket服务器
     */
    @Bean
    public SocketIOServer createSocketIOServer() {
        var config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(hostname);
        config.setPort(port);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingInterval(pingInterval);
        config.setPingTimeout(pingTimeout);
        // 防止同源错误
        config.setOrigin("*");
        config.setTransports(Transport.POLLING, Transport.WEBSOCKET);
        // 避免重启故障
        var socketConfig = config.getSocketConfig();
        socketConfig.setReuseAddress(true);
        // WebSocket 鉴权
        config.setAuthorizationListener(handshakeData -> {
            var token = handshakeData.getSingleUrlParam("token");
            return jwtTokenUtil.validateToken(token);
        });
        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketServer) {
        return new SpringAnnotationScanner(socketServer);
    }
}
