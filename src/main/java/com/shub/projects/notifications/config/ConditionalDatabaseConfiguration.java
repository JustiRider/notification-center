package com.shub.projects.notifications.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Conditional Database Configuration
 * 
 * This configuration is ONLY loaded when notification.database.enabled=true
 * Allows the notification center to work without a database by default
 */
@Configuration
@ConditionalOnProperty(prefix = "notification.database", name = "enabled", havingValue = "true")
@EnableJpaRepositories(basePackages = "com.shub.projects.notifications.repository")
@EntityScan(basePackages = "com.shub.projects.notifications.model")
public class ConditionalDatabaseConfiguration {

    public ConditionalDatabaseConfiguration() {
        System.out.println("Database persistence enabled for Notification Center");
    }
}
