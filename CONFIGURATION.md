# Configuration Guide

## Profiles

### Cache-Only Mode (Default)

Lightweight mode without database dependency.

```yaml
spring:
  profiles:
    active: cache-only
```

### With Database Mode

Enable persistence for notification history.

```yaml
spring:
  profiles:
    active: with-db
```

## Core Configuration

### Server Settings

```yaml
server:
  port: 8090
  servlet:
    context-path: /notifications
  
  # SSL Configuration (Optional)
  ssl:
    enabled: false
    # Uncomment to enable SSL
    # key-store: classpath:keystore.jks
    # key-store-password: your-password
    # key-store-type: JKS
    # key-alias: your-alias
```

### Cache Configuration

```yaml
notification:
  cache:
    enabled: true
    type: caffeine
    caffeine:
      spec: maximumSize=10000,expireAfterWrite=1h
```

### Async Configuration

```yaml
notification:
  async:
    core-pool-size: 5
    max-pool-size: 15
    queue-capacity: 30
```

## Provider Configurations

### WhatsApp (Meta Cloud API)

**Setup Steps:**

1. Create a Meta Business Account
2. Set up WhatsApp Business API
3. Get Phone Number ID and Access Token
4. Configure webhook verify token

**Configuration:**

```yaml
notification:
  whatsapp:
    enabled: true
    apiVersion: v18.0
    phoneNumberId: YOUR_PHONE_NUMBER_ID
    accessToken: YOUR_ACCESS_TOKEN
    webhookVerifyToken: YOUR_VERIFY_TOKEN
    businessAccountId: YOUR_BUSINESS_ACCOUNT_ID
```

**Getting Credentials:**

- Phone Number ID: Found in WhatsApp Business API settings
- Access Token: Generate from Meta Business Suite
- Webhook Verify Token: Create your own secure token

### SMS Configuration

**Twilio Provider:**

```yaml
notification:
  sms:
    enabled: true
    provider: twilio
    accountSid: YOUR_ACCOUNT_SID
    authToken: YOUR_AUTH_TOKEN
    fromNumber: +1234567890
```

**AWS SNS Provider:**

```yaml
notification:
  sms:
    enabled: true
    provider: aws-sns
    # AWS credentials configured via environment or IAM role
```

### Email Configuration

**SMTP (Gmail Example):**

```yaml
notification:
  email:
    enabled: true
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
    from: your-email@gmail.com
    starttlsEnabled: true
    auth: true
```

**Other SMTP Providers:**

```yaml
# Outlook
notification:
  email:
    host: smtp-mail.outlook.com
    port: 587

# SendGrid
notification:
  email:
    host: smtp.sendgrid.net
    port: 587
    username: apikey
    password: YOUR_SENDGRID_API_KEY
```

### Socket.IO Configuration

```yaml
notification:
  socket:
    enabled: true
    host: 0.0.0.0
    port: 3002
```

### RabbitMQ Configuration

```yaml
notification:
  rabbitmq:
    enabled: true
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### AWS S3 Configuration

For media file storage:

```yaml
notification:
  s3:
    enabled: true
    accessKeyId: YOUR_ACCESS_KEY
    secretAccessKey: YOUR_SECRET_KEY
    bucket: your-bucket-name
    region: ap-south-1
```

## Database Configuration

### MySQL

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/notification_center?autoReconnect=true&useSSL=false&createDatabaseIfNotExist=true
    username: root
    password: your-password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

### PostgreSQL

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notification_center
    username: postgres
    password: your-password
    driver-class-name: org.postgresql.Driver
  
  jpa:
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## Environment Variables

For production, use environment variables:

```bash
# WhatsApp
export NOTIFICATION_WHATSAPP_ENABLED=true
export NOTIFICATION_WHATSAPP_PHONE_NUMBER_ID=your-id
export NOTIFICATION_WHATSAPP_ACCESS_TOKEN=your-token

# Database
export SPRING_DATASOURCE_URL=jdbc:mysql://db-host:3306/notification_center
export SPRING_DATASOURCE_USERNAME=dbuser
export SPRING_DATASOURCE_PASSWORD=dbpass

# Server
export SERVER_PORT=8090
export SERVER_SSL_ENABLED=true
export SERVER_SSL_KEY_STORE=/path/to/keystore.jks
export SERVER_SSL_KEY_STORE_PASSWORD=keystorepass
```

## Complete Example Configuration

```yaml
spring:
  application:
    name: notification-center
  profiles:
    active: cache-only

server:
  port: 8090
  servlet:
    context-path: /notifications

notification:
  # Cache (Always enabled for performance)
  cache:
    enabled: true
  
  # Database (Optional)
  database:
    enabled: false
  
  # WhatsApp
  whatsapp:
    enabled: true
    apiVersion: v18.0
    phoneNumberId: ${WHATSAPP_PHONE_NUMBER_ID}
    accessToken: ${WHATSAPP_ACCESS_TOKEN}
    webhookVerifyToken: ${WHATSAPP_VERIFY_TOKEN}
  
  # SMS
  sms:
    enabled: false
  
  # Email
  email:
    enabled: false
  
  # Socket.IO
  socket:
    enabled: false
  
  # RabbitMQ
  rabbitmq:
    enabled: false
  
  # S3
  s3:
    enabled: false
```

## SSL/TLS Setup

### Generate Self-Signed Certificate (Development)

```bash
keytool -genkeypair -alias notification-center -keyalg RSA -keysize 2048 \
  -storetype JKS -keystore keystore.jks -validity 3650 \
  -dname "CN=localhost, OU=Dev, O=ShubProjects, L=City, ST=State, C=IN"
```

### Enable SSL

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.jks
    key-store-password: changeit
    key-store-type: JKS
    key-alias: notification-center
```

## Troubleshooting

### WhatsApp API Errors

- **Invalid Access Token**: Regenerate token from Meta Business Suite
- **Phone Number Not Verified**: Complete phone number verification
- **Template Not Approved**: Wait for template approval or use approved templates

### Database Connection Issues

- Check database is running
- Verify credentials
- Ensure database exists or set `createDatabaseIfNotExist=true`

### Cache Issues

- Check Caffeine dependency is present
- Verify cache configuration syntax
- Review cache statistics in logs
