# Notification Center

**Universal Notification Library & Service** for WhatsApp, SMS, Email, and Socket.IO notifications.

## 🚀 Features

- ✅ **Dual Usage**: Use as a Spring Boot library OR standalone REST API service
- ✅ **WhatsApp**: Latest Meta Cloud API (Graph API v18+)
- ✅ **SMS**: Multiple providers (Twilio, AWS SNS, custom)
- ✅ **Email**: SMTP and API-based sending
- ✅ **Socket.IO**: Real-time push notifications
- ✅ **Cache-First**: Works without database by default
- ✅ **Optional Database**: Enable for persistence and history
- ✅ **Async Support**: Non-blocking notification sending
- ✅ **Template Support**: Pre-configured message templates
- ✅ **Media Support**: Send images, videos, documents

## 📦 Installation

### As a Library (Maven)

```xml
<dependency>
    <groupId>com.shub.projects</groupId>
    <artifactId>notification-center</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### As a Standalone Service

```bash
# Clone the repository
git clone <repository-url>
cd notification-center

# Build the project
mvn clean install

# Run the service
java -jar target/notification-center-1.0.0-SNAPSHOT.jar
```

## 🔧 Configuration

### Cache-Only Mode (Default)

No database required. Perfect for lightweight deployments.

```yaml
spring:
  profiles:
    active: cache-only

notification:
  database:
    enabled: false
```

### With Database (Optional)

Enable persistence for notification history.

```yaml
spring:
  profiles:
    active: with-db

notification:
  database:
    enabled: true
  
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/notification_center
    username: root
    password: your-password
```

### WhatsApp Configuration

```yaml
notification:
  whatsapp:
    enabled: true
    apiVersion: v18.0
    phoneNumberId: your-phone-number-id
    accessToken: your-access-token
    webhookVerifyToken: your-verify-token
```

## 💻 Usage

### As a Library

```java
@Autowired
private NotificationService notificationService;

// Send WhatsApp message
NotificationRequest request = NotificationRequest.builder()
    .type(NotificationType.WHATSAPP)
    .recipient("+1234567890")
    .message("Hello from Notification Center!")
    .build();

NotificationResponse response = notificationService.send(request);
```

### As a REST API

```bash
# Send notification
curl -X POST http://localhost:8090/notifications/api/v1/send \
  -H "Content-Type: application/json" \
  -d '{
    "type": "WHATSAPP",
    "recipient": "+1234567890",
    "message": "Hello from Notification Center!"
  }'
```

## 📚 Documentation

- [Library Usage Guide](LIBRARY_USAGE.md)
- [API Documentation](API_DOCUMENTATION.md)
- [Configuration Guide](CONFIGURATION.md)
- [SMS Provider Configuration](SMS_PROVIDER_CONFIG.md) ✅
- [Postman Testing Guide](POSTMAN_TESTING_GUIDE.md) 🚀

## 🏗️ Architecture

- **Cache-First Design**: Caffeine cache for high performance
- **Provider Pattern**: Pluggable notification providers
- **Conditional Loading**: Optional features loaded based on configuration
- **Auto-Configuration**: Seamless Spring Boot integration

## 🛠️ Technology Stack

- Spring Boot 2.7.17
- Java 11
- Caffeine Cache
- WebFlux (for WhatsApp API)
- Spring Data JPA (optional)
- RabbitMQ (optional)
- Socket.IO (optional)

## 📝 License

Copyright © 2026 Shub Projects

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
