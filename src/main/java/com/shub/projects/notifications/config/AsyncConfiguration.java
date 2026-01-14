package com.shub.projects.notifications.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async Configuration for asynchronous notification processing
 * 
 * Enables non-blocking notification sending for better performance
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

    private final NotificationProperties properties;

    public AsyncConfiguration(NotificationProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getAsync().getCorePoolSize());
        executor.setMaxPoolSize(properties.getAsync().getMaxPoolSize());
        executor.setQueueCapacity(properties.getAsync().getQueueCapacity());
        executor.setThreadNamePrefix("notification-async-");
        executor.initialize();
        return executor;
    }
}
