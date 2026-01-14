# Library Usage Guide

## Using Notification Center as a Spring Boot Library

### 1. Add Maven Dependency

```xml
<dependency>
    <groupId>com.shub.projects</groupId>
    <parameter name="artifactId">notification-center</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 2. Configure in your application.yml

```yaml
notification:
  # WhatsApp Configuration
  whatsapp:
    enabled: true
    apiVersion: v18.0
    phoneNumberId: YOUR_PHONE_NUMBER_ID
    accessToken: YOUR_ACCESS_TOKEN
    webhookVerifyToken: YOUR_WEBHOOK_TOKEN
  
  # Cache Configuration (enabled by default)
  cache:
    enabled: true
  
  # Database (optional, disabled by default)
  database:
    enabled: false
```

### 3. Inject and Use NotificationService

```java
import com.shub.projects.notifications.core.NotificationService;
import com.shub.projects.notifications.dto.NotificationRequest;
import com.shub.projects.notifications.dto.NotificationResponse;
import com.shub.projects.notifications.dto.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class YourService {
    
    @Autowired
    private NotificationService notificationService;
    
    public void sendWelcomeMessage(String phoneNumber) {
        NotificationRequest request = NotificationRequest.builder()
            .type(NotificationType.WHATSAPP)
            .recipient(phoneNumber)
            .message("Welcome to our service!")
            .build();
        
        NotificationResponse response = notificationService.send(request);
        
        if (response.isSuccess()) {
            System.out.println("Message sent! ID: " + response.getMessageId());
        }
    }
}
```

## Advanced Usage

### Sending with Templates

```java
NotificationRequest request = NotificationRequest.builder()
    .type(NotificationType.WHATSAPP)
    .recipient("+1234567890")
    .templateId("welcome_template")
    .templateParameters(Map.of(
        "name", "John Doe",
        "code", "ABC123"
    ))
    .build();

notificationService.send(request);
```

### Sending with Media

```java
NotificationRequest.MediaAttachment media = NotificationRequest.MediaAttachment.builder()
    .url("https://example.com/image.jpg")
    .type("image")
    .caption("Check out this image!")
    .build();

NotificationRequest request = NotificationRequest.builder()
    .type(NotificationType.WHATSAPP)
    .recipient("+1234567890")
    .media(media)
    .build();

notificationService.send(request);
```

### Async Sending

```java
CompletableFuture<NotificationResponse> future = 
    notificationService.sendAsync(request);

future.thenAccept(response -> {
    System.out.println("Async message sent: " + response.getMessageId());
});
```

### Bulk Sending

```java
List<NotificationRequest> requests = Arrays.asList(
    NotificationRequest.builder()
        .type(NotificationType.WHATSAPP)
        .recipient("+1234567890")
        .message("Message 1")
        .build(),
    NotificationRequest.builder()
        .type(NotificationType.WHATSAPP)
        .recipient("+0987654321")
        .message("Message 2")
        .build()
);

CompletableFuture<List<NotificationResponse>> future = 
    notificationService.sendBulk(requests);
```

## Auto-Configuration

The notification center uses Spring Boot auto-configuration. When you add the dependency, it automatically:

1. Scans for `notification.*` properties
2. Loads providers based on enabled flags
3. Configures cache (Caffeine by default)
4. Sets up async executor
5. Conditionally loads database support

No additional configuration class needed!
