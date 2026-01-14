package com.shub.projects.notifications.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration using Caffeine
 * 
 * Provides high-performance in-memory caching for:
 * - Notification templates
 * - Recent notification history
 * - Provider configurations
 * - WhatsApp message status
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "notification.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CacheConfiguration {

    private final NotificationProperties properties;

    public CacheConfiguration(NotificationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "notifications",
                "templates",
                "whatsapp-status",
                "configurations");

        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats();
    }
}
