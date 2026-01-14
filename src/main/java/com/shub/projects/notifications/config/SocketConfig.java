package com.shub.projects.notifications.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;

/**
 * Socket.IO Configuration
 * 
 * Loads Socket.IO settings from application.yml and creates SocketIOServer bean
 */
@Data
@org.springframework.context.annotation.Configuration
@ConfigurationProperties(prefix = "notification.socket")
@ConditionalOnProperty(prefix = "notification.socket", name = "enabled", havingValue = "true")
public class SocketConfig {

    private static final Logger log = LoggerFactory.getLogger(SocketConfig.class);

    /**
     * Enable/disable socket notifications
     */
    private boolean enabled = false;

    /**
     * Socket.IO server host
     */
    private String host = "0.0.0.0";

    /**
     * Socket.IO server port
     */
    private int port = 3002;

    private SocketIOServer server;

    /**
     * Create and configure Socket.IO server
     */
    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);

        server = new SocketIOServer(config);
        server.start();

        server.addConnectListener(client -> log.info("New socket client connected: sessionId={}, time={}",
                client.getSessionId(),
                client.getHandshakeData().getTime()));

        server.addDisconnectListener(
                client -> log.info("Socket client disconnected: sessionId={}", client.getSessionId()));

        log.info("Socket.IO server started on {}:{}", host, port);
        return server;
    }

    /**
     * Cleanup: Stop Socket.IO server on application shutdown
     */
    @PreDestroy
    public void stopSocketIOServer() {
        if (server != null) {
            log.info("Stopping Socket.IO server...");
            server.stop();
        }
    }
}
