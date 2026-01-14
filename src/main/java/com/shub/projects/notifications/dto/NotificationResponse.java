package com.shub.projects.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Notification Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private boolean success;
    private String messageId;
    private String status;
    private String errorMessage;
    private LocalDateTime timestamp;
    private Map<String, Object> providerResponse;

    public static NotificationResponse success(String messageId) {
        return NotificationResponse.builder()
                .success(true)
                .messageId(messageId)
                .status("SENT")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static NotificationResponse failure(String errorMessage) {
        return NotificationResponse.builder()
                .success(false)
                .status("FAILED")
                .errorMessage(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
