package com.shub.projects.notifications.provider.sms;

import com.shub.projects.notifications.config.SmsConfig;
import com.shub.projects.notifications.dto.NotificationRequest;
import com.shub.projects.notifications.dto.NotificationResponse;
import com.shub.projects.notifications.provider.NotificationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * SMS Notification Provider
 * 
 * Sends SMS notifications via configured SMS gateway
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notification.sms", name = "enabled", havingValue = "true")
public class SmsProvider implements NotificationProvider {

    private final SmsConfig smsConfig;
    private final WebClient.Builder webClientBuilder;

    @Override
    public String getType() {
        return "SMS";
    }

    @Override
    public boolean isEnabled() {
        return smsConfig.isEnabled();
    }

    @Override
    public NotificationResponse send(NotificationRequest request) {
        log.info("Sending SMS to: {}", request.getRecipient());

        try {
            // Encode message
            String encodedMessage = URLEncoder.encode(request.getMessage(), StandardCharsets.UTF_8);

            // Build URL with parameters
            String url = buildSmsUrl(request, encodedMessage);

            log.debug("SMS Gateway URL: {}", url);

            // Send SMS via HTTP POST request (as per recent config change)
            WebClient webClient = webClientBuilder.build();
            String response = webClient.post()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("SMS Gateway Response: {}", response);

            // Parse response to check for success/failure
            boolean success = false;
            String errorMessage = null;
            String messageId = generateMessageId();

            try {
                // Determine success based on response content
                // SMSGatewayHub returns JSON: {"ErrorCode":"000", "ErrorMessage":"Success",
                // ...}
                // or failure: {"ErrorCode":"006", "ErrorMessage":"error:Invalid template text",
                // ...}
                if (response != null && response.contains("ErrorCode")) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    Map<String, Object> map = mapper.readValue(response, Map.class);

                    if (map.containsKey("ErrorMessage")) {
                        String msg = map.get("ErrorMessage").toString();
                        if ("Success".equalsIgnoreCase(msg)) {
                            success = true;
                        } else {
                            success = false;
                            errorMessage = msg;
                        }
                    } else if (map.containsKey("ErrorCode")) {
                        String code = map.get("ErrorCode").toString();
                        if ("000".equals(code) || "0".equals(code)) {
                            success = true;
                        } else {
                            success = false;
                            errorMessage = "ErrorCode: " + code;
                        }
                    } else {
                        // Fallback check
                        success = true;
                    }

                    if (map.containsKey("JobId") && !ObjectUtils.isEmpty(map.get("JobId"))) {
                        // Use JobId as messageId if available
                        // But we already generated one, maybe append it?
                        // Keeping internal messageId for consistency
                    }
                } else if (response != null && response.toLowerCase().contains("success")) {
                    // Fallback string check
                    success = true;
                } else {
                    // Conservative fallback - if we got 200 OK but couldn't parse logic, assume
                    // success?
                    // Or let's just default to what we parsed.
                    // If response is "error...", fail.
                    if (response != null && response.toLowerCase().startsWith("error")) {
                        success = false;
                        errorMessage = response;
                    } else {
                        success = true; // Default success for unknown formats (legacy behavior)
                    }
                }
            } catch (Exception parseEx) {
                log.warn("Failed to parse SMS response: {}", parseEx.getMessage());
                // If we can't parse, but HTTP was 200, assume success but log warning
                success = true;
            }

            return NotificationResponse.builder()
                    .success(success)
                    .status(success ? "SENT" : "FAILED")
                    .errorMessage(errorMessage)
                    .messageId(messageId)
                    .providerResponse(Map.of("rawResponse", response != null ? response : ""))
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Failed to send SMS", e);
            return NotificationResponse.builder()
                    .success(false)
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * Build SMS gateway URL by replacing template parameters
     */
    private String buildSmsUrl(NotificationRequest request, String encodedMessage) {
        String url = smsConfig.getUrl();
        String params = smsConfig.getParams();

        // Build parameter map with all provider variations
        Map<String, String> paramMap = new HashMap<>();
        // MSG91 style
        paramMap.put("authkey", smsConfig.getAuthKey());
        paramMap.put("mobiles", request.getRecipient());
        // SMSGatewayHub style
        paramMap.put("APIKey", smsConfig.getAuthKey());
        paramMap.put("apikey", smsConfig.getAuthKey());
        paramMap.put("user", smsConfig.getUser());
        paramMap.put("password", smsConfig.getPassword());
        paramMap.put("number", request.getRecipient());
        // Message variations
        paramMap.put("message", encodedMessage);
        paramMap.put("text", encodedMessage);
        // Sender variations
        paramMap.put("sender", smsConfig.getSenderId());
        paramMap.put("senderid", smsConfig.getSenderId());
        // Route and channel
        paramMap.put("route", smsConfig.getRoute());
        paramMap.put("channel", smsConfig.getChannel());
        // DLT parameters (India)
        paramMap.put("dltEntityId", smsConfig.getDltEntityId());
        paramMap.put("dltTemplateId",
                request.getMetadata() != null && request.getMetadata().containsKey("dltTemplateId")
                        ? request.getMetadata().get("dltTemplateId").toString()
                        : smsConfig.getDltTemplateId());

        // Replace template variables in URL first (for providers that use path
        // parameters)
        String finalUrl = url;
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (!ObjectUtils.isEmpty(entry.getValue())) {
                finalUrl = finalUrl.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        // Replace template variables in params
        String finalParams = params;
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (!ObjectUtils.isEmpty(entry.getValue())) {
                finalParams = finalParams.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return finalUrl + (params.startsWith("?") ? "" : "?") + finalParams;
    }

    /**
     * Generate unique message ID
     */
    private String generateMessageId() {
        return "SMS_" + System.currentTimeMillis() + "_" +
                (int) (Math.random() * 10000);
    }
}
