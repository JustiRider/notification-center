package com.shub.projects.notifications.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Notification Center
 */
@Data
@ConfigurationProperties(prefix = "notification")
public class NotificationProperties {

    private DatabaseConfig database = new DatabaseConfig();
    private CacheConfig cache = new CacheConfig();
    private AsyncConfig async = new AsyncConfig();
    private RabbitMQConfig rabbitmq = new RabbitMQConfig();
    // private SocketConfig socket = new SocketConfig(); // Handled by
    // com.shub.projects.notifications.config.SocketConfig
    private S3Config s3 = new S3Config();
    private WhatsAppConfig whatsapp = new WhatsAppConfig();
    // private SmsConfig sms = new SmsConfig(); // Handled by
    // com.shub.projects.notifications.config.SmsConfig
    // private EmailConfig email = new EmailConfig(); // Handled by
    // com.shub.projects.notifications.config.EmailConfig

    @Data
    public static class DatabaseConfig {
        private boolean enabled = false;
    }

    @Data
    public static class CacheConfig {
        private boolean enabled = true;
        private String type = "caffeine";
        private CaffeineSpec caffeine = new CaffeineSpec();

        @Data
        public static class CaffeineSpec {
            private String spec = "maximumSize=10000,expireAfterWrite=1h";
        }
    }

    @Data
    public static class AsyncConfig {
        private int corePoolSize = 5;
        private int maxPoolSize = 15;
        private int queueCapacity = 30;
    }

    @Data
    public static class RabbitMQConfig {
        private boolean enabled = false;
        private String host = "localhost";
        private int port = 5672;
        private String username = "guest";
        private String password = "guest";
    }

    @Data
    public static class SocketConfig {
        private boolean enabled = false;
        private String host = "0.0.0.0";
        private int port = 3002;
    }

    @Data
    public static class S3Config {
        private boolean enabled = false;
        private String accessKeyId;
        private String secretAccessKey;
        private String bucket;
        private String region = "ap-south-1";
    }

    @Data
    public static class WhatsAppConfig {
        private boolean enabled = false;
        private String apiVersion = "v18.0";
        private String phoneNumberId;
        private String accessToken;
        private String webhookVerifyToken;
        private String businessAccountId;
    }

    @Data
    public static class SmsConfig {
        private boolean enabled = false;
        private String provider = "twilio"; // twilio, aws-sns, custom
        private String accountSid;
        private String authToken;
        private String fromNumber;
    }

    @Data
    public static class EmailConfig {
        private boolean enabled = false;
        private String host = "smtp.gmail.com";
        private int port = 587;
        private String username;
        private String password;
        private String from;
        private boolean starttlsEnabled = true;
        private boolean auth = true;
    }
}
