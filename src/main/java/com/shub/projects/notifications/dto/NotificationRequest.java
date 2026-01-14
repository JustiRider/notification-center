package com.shub.projects.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Generic Notification Request DTO
 * 
 * Used for sending notifications through any provider
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NotBlank(message = "Notification type is required")
    private String type; // e.g., "WHATSAPP", "SMS", "EMAIL", "SOCKET"

    @NotBlank(message = "Recipient is required")
    private String recipient;

    private String subject;

    @NotBlank(message = "Message content is required")
    private String message;

    private Map<String, Object> metadata;

    private String templateId;

    private Map<String, String> templateParameters;

    private MediaAttachment media;

    @Builder.Default
    private Priority priority = Priority.NORMAL;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaAttachment {
        private String url;
        private String type; // image, video, document, audio
        private String filename;
        private String caption;
    }

    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }
}
