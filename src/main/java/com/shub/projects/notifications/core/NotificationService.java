package com.shub.projects.notifications.core;

import com.shub.projects.notifications.dto.NotificationRequest;
import com.shub.projects.notifications.dto.NotificationResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Main Notification Service Interface
 * 
 * This is the primary interface for sending notifications.
 * Use this when integrating the notification center as a library.
 */
public interface NotificationService {

    /**
     * Send a notification synchronously
     * 
     * @param request Notification request
     * @return Notification response
     */
    NotificationResponse send(NotificationRequest request);

    /**
     * Send a notification asynchronously
     * 
     * @param request Notification request
     * @return CompletableFuture with notification response
     */
    CompletableFuture<NotificationResponse> sendAsync(NotificationRequest request);

    /**
     * Send bulk notifications asynchronously
     * 
     * @param requests Multiple notification requests
     * @return CompletableFuture with list of responses
     */
    CompletableFuture<java.util.List<NotificationResponse>> sendBulk(java.util.List<NotificationRequest> requests);
}
