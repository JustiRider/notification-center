package com.shub.projects.notifications.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Email Provider Configuration
 * 
 * Loads email SMTP settings from application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "notification.email")
public class EmailConfig {

    /**
     * Enable/disable email notifications
     */
    private boolean enabled = false;

    /**
     * SMTP server host
     */
    private String host;

    /**
     * SMTP server port
     */
    private int port = 587;

    /**
     * Email account username
     */
    private String username;

    /**
     * Email account password
     */
    private String password;

    /**
     * Protocol (smtp, smtps)
     */
    private String protocol = "smtp";

    /**
     * SMTP authentication property key
     */
    private String authPropertyKey = "mail.smtp.auth";

    /**
     * SMTP authentication property value
     */
    private String authPropertyValue = "true";

    /**
     * SSL enable property key
     */
    private String sslEnablePropertyKey = "mail.smtp.ssl.enable";

    /**
     * SSL enable property value
     */
    private String sslEnablePropertyValue = "true";

    /**
     * SSL trust property key
     */
    private String sslTrustPropertyKey = "mail.smtp.ssl.trust";

    /**
     * SSL trust property value
     */
    private String sslTrustPropertyValue = "*";

    /**
     * Default from email address
     */
    private String fromEmail;
}
