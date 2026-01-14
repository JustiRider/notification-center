package com.shub.projects.notifications.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * SMS Provider Configuration
 * 
 * Loads SMS provider settings from application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "notification.sms")
public class SmsConfig {

    /**
     * Enable/disable SMS notifications
     */
    private boolean enabled = false;

    /**
     * SMS provider type (MSG91, IGLOBE, TWILIO, etc.)
     */
    private String provider;

    /**
     * SMS gateway base URL
     */
    private String url;

    /**
     * URL parameters template
     * Example:
     * "authkey={authkey}&mobiles={mobiles}&message={message}&sender={sender}&route={route}&DLT_TE_ID={dltTemplateId}"
     */
    private String params;

    /**
     * Authentication key/API key for SMS gateway (MSG91, etc.)
     */
    private String authKey;

    /**
     * Username for SMS gateway (SMSGatewayHub uses this)
     */
    private String user;

    /**
     * Password for SMS gateway (SMSGatewayHub uses this)
     */
    private String password;

    /**
     * Sender ID for SMS
     */
    private String senderId;

    /**
     * DLT (Distributed Ledger Technology) Entity ID for regulatory compliance
     */
    private String dltEntityId;

    /**
     * Default DLT Template ID
     */
    private String dltTemplateId;

    /**
     * SMS route (transactional, promotional, etc.)
     */
    private String route = "4";

    /**
     * Message channel for SMSGatewayHub (Promotional=1, Transactional=2, OTP=OTP)
     */
    private String channel;

    /**
     * Maximum characters per SMS
     */
    private int maxCharactersPerSms = 160;
}
