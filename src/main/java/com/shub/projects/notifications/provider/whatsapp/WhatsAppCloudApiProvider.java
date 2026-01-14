package com.shub.projects.notifications.provider.whatsapp;

import com.shub.projects.notifications.config.NotificationProperties;
import com.shub.projects.notifications.dto.NotificationRequest;
import com.shub.projects.notifications.dto.NotificationResponse;
import com.shub.projects.notifications.exception.NotificationException;
import com.shub.projects.notifications.provider.NotificationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * WhatsApp Cloud API Provider
 * 
 * Uses Meta's WhatsApp Business Cloud API (Graph API v18+)
 * Documentation: https://developers.facebook.com/docs/whatsapp/cloud-api
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "notification.whatsapp", name = "enabled", havingValue = "true")
public class WhatsAppCloudApiProvider implements NotificationProvider {

    private final NotificationProperties properties;
    private final WebClient webClient;
    private final String baseUrl;

    public WhatsAppCloudApiProvider(NotificationProperties properties) {
        this.properties = properties;
        this.baseUrl = String.format("https://graph.facebook.com/%s/%s",
                properties.getWhatsapp().getApiVersion(),
                properties.getWhatsapp().getPhoneNumberId());

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION,
                        "Bearer " + properties.getWhatsapp().getAccessToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        log.info("WhatsApp Cloud API Provider initialized with API version: {}",
                properties.getWhatsapp().getApiVersion());
    }

    @Override
    public String getType() {
        return "WHATSAPP";
    }

    @Override
    public NotificationResponse send(NotificationRequest request) {
        try {
            Map<String, Object> payload = buildMessagePayload(request);

            Map<String, Object> response = webClient.post()
                    .uri("/messages")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("messages")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> messages = (Map<String, Object>) ((java.util.List<?>) response.get("messages"))
                        .get(0);
                String messageId = (String) messages.get("id");

                log.info("WhatsApp message sent successfully. MessageId: {}", messageId);
                return NotificationResponse.builder()
                        .success(true)
                        .messageId(messageId)
                        .status("SENT")
                        .providerResponse(response)
                        .timestamp(java.time.LocalDateTime.now())
                        .build();
            }

            throw new NotificationException("Invalid response from WhatsApp API");

        } catch (Exception e) {
            log.error("Failed to send WhatsApp message: {}", e.getMessage(), e);
            return NotificationResponse.failure("WhatsApp send failed: " + e.getMessage());
        }
    }

    @Override
    public boolean isEnabled() {
        return properties.getWhatsapp().isEnabled();
    }

    /**
     * Build message payload according to WhatsApp Cloud API specification
     */
    private Map<String, Object> buildMessagePayload(NotificationRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", normalizePhoneNumber(request.getRecipient()));

        // If template is specified, use template message
        if (request.getTemplateId() != null) {
            payload.put("type", "template");
            payload.put("template", buildTemplateObject(request));
        }
        // If media is attached, use media message
        else if (request.getMedia() != null) {
            payload.put("type", request.getMedia().getType());
            payload.put(request.getMedia().getType(), buildMediaObject(request));
        }
        // Otherwise, send text message
        else {
            payload.put("type", "text");
            Map<String, Object> text = new HashMap<>();
            text.put("preview_url", false);
            text.put("body", request.getMessage());
            payload.put("text", text);
        }

        return payload;
    }

    private Map<String, Object> buildTemplateObject(NotificationRequest request) {
        Map<String, Object> template = new HashMap<>();
        template.put("name", request.getTemplateId());
        template.put("language", Map.of("code", "en"));

        if (request.getTemplateParameters() != null && !request.getTemplateParameters().isEmpty()) {
            java.util.List<Map<String, String>> components = new java.util.ArrayList<>();
            Map<String, Object> bodyComponent = new HashMap<>();
            bodyComponent.put("type", "body");

            java.util.List<Map<String, String>> parameters = new java.util.ArrayList<>();
            request.getTemplateParameters().forEach((key, value) -> {
                parameters.add(Map.of("type", "text", "text", value));
            });
            bodyComponent.put("parameters", parameters);
            components.add((Map<String, String>) (Object) bodyComponent);
            template.put("components", components);
        }

        return template;
    }

    private Map<String, Object> buildMediaObject(NotificationRequest request) {
        Map<String, Object> media = new HashMap<>();
        media.put("link", request.getMedia().getUrl());

        if (request.getMedia().getCaption() != null) {
            media.put("caption", request.getMedia().getCaption());
        }

        if (request.getMedia().getFilename() != null) {
            media.put("filename", request.getMedia().getFilename());
        }

        return media;
    }

    /**
     * Normalize phone number to E.164 format
     * WhatsApp requires phone numbers without + prefix
     */
    private String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber.replaceAll("[^0-9]", "");
    }
}
