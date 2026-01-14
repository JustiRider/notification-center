package com.shub.projects.notifications.provider.socket;

import com.corundumstudio.socketio.SocketIOServer;
import com.shub.projects.notifications.config.SocketConfig;
import com.shub.projects.notifications.dto.NotificationRequest;
import com.shub.projects.notifications.dto.NotificationResponse;
import com.shub.projects.notifications.provider.NotificationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Socket.IO Notification Provider
 * 
 * Broadcasts notifications to connected Socket.IO clients
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notification.socket", name = "enabled", havingValue = "true")
public class SocketProvider implements NotificationProvider {

    private static final String ROOM_PREFIX = "room-";
    private static final String DEFAULT_EVENT = "notification";

    private final SocketIOServer socketServer;
    private final SocketConfig socketConfig;

    @Override
    public String getType() {
        return "SOCKET";
    }

    @Override
    public boolean isEnabled() {
        return socketConfig.isEnabled();
    }

    @Override
    public NotificationResponse send(NotificationRequest request) {
        log.info("Broadcasting socket notification to: {}", request.getRecipient());

        try {
            String eventName = getEventName(request);

            // Check if recipient is a room or broadcast to all
            if (request.getRecipient().startsWith("room:")) {
                // Send to specific room
                String roomId = request.getRecipient().substring(5); // Remove "room:" prefix
                sendToRoom(roomId, eventName, request);
            } else if ("broadcast".equalsIgnoreCase(request.getRecipient()) ||
                    "all".equalsIgnoreCase(request.getRecipient())) {
                // Broadcast to all connected clients
                broadcastToAll(eventName, request);
            } else {
                // Send to specific session ID
                sendToSession(request.getRecipient(), eventName, request);
            }

            log.info("Socket notification sent successfully");

            return NotificationResponse.builder()
                    .success(true)
                    .messageId(generateMessageId())
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Failed to send socket notification", e);
            return NotificationResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * Send notification to a specific room
     */
    private void sendToRoom(String roomId, String eventName, NotificationRequest request) {
        log.debug("Sending to room: {}", roomId);
        socketServer.getRoomOperations(ROOM_PREFIX + roomId)
                .sendEvent(eventName, buildSocketMessage(request));
    }

    /**
     * Broadcast notification to all connected clients
     */
    private void broadcastToAll(String eventName, NotificationRequest request) {
        log.debug("Broadcasting to all clients");
        socketServer.getBroadcastOperations()
                .sendEvent(eventName, buildSocketMessage(request));
    }

    /**
     * Send notification to a specific session
     */
    private void sendToSession(String sessionId, String eventName, NotificationRequest request) {
        log.debug("Sending to session: {}", sessionId);
        socketServer.getClient(java.util.UUID.fromString(sessionId))
                .sendEvent(eventName, buildSocketMessage(request));
    }

    /**
     * Build socket message object
     */
    private Object buildSocketMessage(NotificationRequest request) {
        if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
            // If metadata exists, send the full request
            return request;
        } else {
            // Otherwise, just send the message
            return request.getMessage();
        }
    }

    /**
     * Get event name from metadata or use default
     */
    private String getEventName(NotificationRequest request) {
        if (request.getMetadata() != null && request.getMetadata().containsKey("event")) {
            return request.getMetadata().get("event").toString();
        }
        return DEFAULT_EVENT;
    }

    /**
     * Generate unique message ID
     */
    private String generateMessageId() {
        return "SOCKET_" + System.currentTimeMillis() + "_" +
                (int) (Math.random() * 10000);
    }
}
