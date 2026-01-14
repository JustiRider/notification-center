package com.shub.projects.notifications.core;

import com.shub.projects.notifications.dto.NotificationRequest;
import com.shub.projects.notifications.dto.NotificationResponse;
import com.shub.projects.notifications.exception.NotificationException;
import com.shub.projects.notifications.provider.NotificationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Default implementation of NotificationService
 * 
 * Routes notifications to appropriate providers based on type
 */
@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final Map<String, NotificationProvider> providers;

    public NotificationServiceImpl(List<NotificationProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        NotificationProvider::getType,
                        provider -> provider));
        log.info("Initialized NotificationService with providers: {}", providers.keySet());
    }

    @Override
    public NotificationResponse send(NotificationRequest request) {
        try {
            log.debug("Sending {} notification to {}", request.getType(), request.getRecipient());

            NotificationProvider provider = getProvider(request.getType());
            NotificationResponse response = provider.send(request);

            if (response.isSuccess()) {
                log.info("Notification sent successfully. MessageId: {}", response.getMessageId());
            } else {
                log.error("Failed to send notification. Provider Error: {}", response.getErrorMessage());
            }

            return response;

        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage(), e);
            return NotificationResponse.failure(e.getMessage());
        }
    }

    @Override
    @Async("notificationExecutor")
    public CompletableFuture<NotificationResponse> sendAsync(NotificationRequest request) {
        return CompletableFuture.completedFuture(send(request));
    }

    @Override
    @Async("notificationExecutor")
    public CompletableFuture<List<NotificationResponse>> sendBulk(List<NotificationRequest> requests) {
        List<NotificationResponse> responses = requests.stream()
                .map(this::send)
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(responses);
    }

    private NotificationProvider getProvider(String type) {
        NotificationProvider provider = providers.get(type.toUpperCase());
        if (provider == null) {
            throw new NotificationException("No provider configured for type: " + type);
        }
        return provider;
    }
}
