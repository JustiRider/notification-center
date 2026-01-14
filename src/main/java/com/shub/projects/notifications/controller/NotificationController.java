package com.shub.projects.notifications.controller;

import com.shub.projects.notifications.core.NotificationService;
import com.shub.projects.notifications.dto.NotificationRequest;
import com.shub.projects.notifications.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main Notification Controller
 * 
 * REST API endpoints for sending notifications
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(maxAge = 3600, origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Send a single notification
     * 
     * POST /api/v1/send
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        log.info("Received notification request: type={}, recipient={}",
                request.getType(), request.getRecipient());

        NotificationResponse response = notificationService.send(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Send a notification asynchronously
     * 
     * POST /api/v1/send/async
     */
    @PostMapping("/send/async")
    public CompletableFuture<ResponseEntity<NotificationResponse>> sendNotificationAsync(
            @Valid @RequestBody NotificationRequest request) {
        log.info("Received async notification request: type={}, recipient={}",
                request.getType(), request.getRecipient());

        return notificationService.sendAsync(request)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Send bulk notifications
     * 
     * POST /api/v1/send/bulk
     */
    @PostMapping("/send/bulk")
    public CompletableFuture<ResponseEntity<List<NotificationResponse>>> sendBulkNotifications(
            @Valid @RequestBody List<NotificationRequest> requests) {
        log.info("Received bulk notification request: count={}", requests.size());

        return notificationService.sendBulk(requests)
                .thenApply(ResponseEntity::ok);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Notification Center is running");
    }
}
