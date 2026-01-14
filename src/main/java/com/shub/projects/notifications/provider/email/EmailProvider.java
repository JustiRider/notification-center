package com.shub.projects.notifications.provider.email;

import com.shub.projects.notifications.config.EmailConfig;
import com.shub.projects.notifications.dto.NotificationRequest;
import com.shub.projects.notifications.dto.NotificationResponse;
import com.shub.projects.notifications.provider.NotificationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

/**
 * Email Notification Provider
 * 
 * Sends email notifications via configured SMTP server
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "notification.email", name = "enabled", havingValue = "true")
public class EmailProvider implements NotificationProvider {

    private final EmailConfig emailConfig;

    @Override
    public String getType() {
        return "EMAIL";
    }

    @Override
    public boolean isEnabled() {
        return emailConfig.isEnabled();
    }

    @Override
    public NotificationResponse send(NotificationRequest request) {
        log.info("Sending email to: {}", request.getRecipient());

        try {
            // Create mail sender
            JavaMailSenderImpl mailSender = createMailSender();

            // Create MIME message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            // Set email properties
            helper.setFrom(ObjectUtils.isEmpty(emailConfig.getFromEmail())
                    ? emailConfig.getUsername()
                    : emailConfig.getFromEmail());
            helper.setTo(request.getRecipient());
            helper.setSubject(request.getSubject() != null ? request.getSubject() : "Notification");
            helper.setText(request.getMessage(), isHtmlContent(request.getMessage()));

            // Add attachments if present
            if (request.getMedia() != null && !ObjectUtils.isEmpty(request.getMedia().getUrl())) {
                File attachment = new File(request.getMedia().getUrl());
                if (attachment.exists()) {
                    helper.addAttachment(
                            ObjectUtils.isEmpty(request.getMedia().getFilename())
                                    ? attachment.getName()
                                    : request.getMedia().getFilename(),
                            attachment);
                }
            }

            // Add multiple attachments from metadata if present
            if (request.getMetadata() != null && request.getMetadata().containsKey("attachments")) {
                @SuppressWarnings("unchecked")
                List<String> attachments = (List<String>) request.getMetadata().get("attachments");
                for (String attachmentPath : attachments) {
                    File file = new File(attachmentPath);
                    if (file.exists()) {
                        helper.addAttachment(file.getName(), file);
                    }
                }
            }

            // Send email
            mailSender.send(mimeMessage);

            log.info("Email sent successfully to: {}", request.getRecipient());

            return NotificationResponse.builder()
                    .success(true)
                    .messageId(generateMessageId())
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (MessagingException e) {
            log.error("Failed to send email", e);
            return NotificationResponse.builder()
                    .success(false)
                    .errorMessage("Messaging error: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("Failed to send email", e);
            return NotificationResponse.builder()
                    .success(false)
                    .errorMessage(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * Create and configure JavaMailSender with SMTP settings
     */
    private JavaMailSenderImpl createMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        mailSender.setHost(emailConfig.getHost());
        mailSender.setPort(emailConfig.getPort());
        mailSender.setUsername(emailConfig.getUsername());
        mailSender.setPassword(emailConfig.getPassword());
        mailSender.setProtocol(emailConfig.getProtocol());

        // Configure mail properties
        Properties props = new Properties();
        props.put(emailConfig.getAuthPropertyKey(), emailConfig.getAuthPropertyValue());
        props.put(emailConfig.getSslEnablePropertyKey(), emailConfig.getSslEnablePropertyValue());
        props.put(emailConfig.getSslTrustPropertyKey(), emailConfig.getSslTrustPropertyValue());
        props.put("mail.debug", "false");

        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

    /**
     * Check if content is HTML
     */
    private boolean isHtmlContent(String message) {
        return message != null &&
                (message.trim().startsWith("<html") ||
                        message.trim().startsWith("<!DOCTYPE") ||
                        message.contains("<body") ||
                        message.contains("<div"));
    }

    /**
     * Generate unique message ID
     */
    private String generateMessageId() {
        return "EMAIL_" + System.currentTimeMillis() + "_" +
                (int) (Math.random() * 10000);
    }
}
