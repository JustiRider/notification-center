package com.shub.projects.notifications.provider;

import com.shub.projects.notifications.dto.NotificationRequest;
import com.shub.projects.notifications.dto.NotificationResponse;
import com.shub.projects.notifications.dto.NotificationType;

/**
 * Base interface for all notification providers
 * 
 * Implement this interface to create custom notification providers
 */
public interface NotificationProvider {

    /**
     * Get the notification type this provider handles
     * 
     * @return Type identifier (e.g., "WHATSAPP", "SMS", "EMAIL")
     */
    String getType();

    /**
     * Send a notification
     * 
     * @param request Notification request
     * @return Notification response
     */
    NotificationResponse send(NotificationRequest request);

    /**
     * Check if this provider is enabled and configured
     */
    boolean isEnabled();
}
