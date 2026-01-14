package com.shub.projects.notifications;

import com.shub.projects.notifications.config.NotificationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Notification Center - Universal Notification Library & Service
 * 
 * This application can be used in two ways:
 * 1. As a Spring Boot Library - Include as a Maven dependency
 * 2. As a Standalone REST API Service - Deploy and call via HTTP
 * 
 * Features:
 * - WhatsApp notifications (using Meta Cloud API)
 * - SMS notifications (multiple providers)
 * - Email notifications
 * - Socket.IO real-time notifications
 * - Cache-first architecture (works without database)
 * - Optional database for persistence
 * 
 * @author Shub Projects
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
@EnableCaching
@EnableConfigurationProperties(NotificationProperties.class)
public class NotificationCenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationCenterApplication.class, args);
    }
}
