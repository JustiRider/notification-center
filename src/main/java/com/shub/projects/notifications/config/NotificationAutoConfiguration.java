package com.shub.projects.notifications.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for Notification Center
 * 
 * This configuration is automatically loaded when the notification-center
 * is used as a library in another Spring Boot application.
 * 
 * It enables all notification features based on configuration properties.
 */
@Configuration
@ComponentScan(basePackages = "com.shub.projects.notifications")
@EnableConfigurationProperties(NotificationProperties.class)
@Import({
        CacheConfiguration.class,
        AsyncConfiguration.class,
        ConditionalDatabaseConfiguration.class
})
public class NotificationAutoConfiguration {

    public NotificationAutoConfiguration() {
        // Log that auto-configuration is active
        System.out.println("Notification Center Auto-Configuration Activated");
    }
}
