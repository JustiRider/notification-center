# Postman API Testing Guide for Notification Center

## Quick Start - Test SMS with SMSGatewayHub

You've configured SMSGatewayHub as your SMS provider. Here's how to test it in Postman:

### 1. Basic Setup

**Base URL:** `http://localhost:9090/notifications`

**Endpoint:** `POST /api/v1/send`

**Headers:**
```
Content-Type: application/json
```

---

## SMS Test Request (SMSGatewayHub)

### Postman Configuration

1. **Method:** `POST`
2. **URL:** `http://localhost:9090/notifications/api/v1/send`
3. **Headers:** Set `Content-Type` to `application/json`
4. **Body:** Select `raw` and choose `JSON` format

### Request Body

```json
{
  "type": "SMS",
  "recipient": "919876543210",
  "message": "Hello! This is a test message from Notification Center."
}
```

### With DLT Template Override (Optional)

```json
{
  "type": "SMS",
  "recipient": "919876543210",
  "message": "Your OTP is 123456. Valid for 5 minutes.",
  "metadata": {
    "dltTemplateId": "1234567890123456789"
  }
}
```

---

## Expected Response

### Success Response
```json
{
  "success": true,
  "messageId": "SMS_1736842832000_4567",
  "status": null,
  "errorMessage": null,
  "timestamp": "2026-01-14T12:10:32.123456",
  "providerResponse": {
    "rawResponse": "<?xml version=\"1.0\"?><response><status>success</status><message>SMS sent successfully</message></response>"
  }
}
```

### Error Response (If SMS disabled)
```json
{
  "success": false,
  "status": "FAILED",
  "errorMessage": "No provider configured for type: SMS",
  "timestamp": "2026-01-14T12:10:32.123456"
}
```

---

## All Available Endpoints

### 1. Send Single Notification (Synchronous)
```
POST http://localhost:9090/notifications/api/v1/send
```

### 2. Send Notification (Asynchronous)
```
POST http://localhost:9090/notifications/api/v1/send/async
```

### 3. Send Bulk Notifications
```
POST http://localhost:9090/notifications/api/v1/send/bulk
```

**Bulk Request Body Example:**
```json
[
  {
    "type": "SMS",
    "recipient": "919876543210",
    "message": "Message 1"
  },
  {
    "type": "SMS",
    "recipient": "919876543211",
    "message": "Message 2"
  }
]
```

### 4. Health Check
```
GET http://localhost:9090/notifications/api/v1/health
```

---

## Testing Different Providers

### SMS Example
```json
{
  "type": "SMS",
  "recipient": "919876543210",
  "message": "Test SMS message"
}
```

### Email Example
```json
{
  "type": "EMAIL",
  "recipient": "user@example.com",
  "subject": "Test Email",
  "message": "<h1>Hello!</h1><p>This is a test email.</p>"
}
```

### Socket Example (Broadcast)
```json
{
  "type": "SOCKET",
  "recipient": "broadcast",
  "message": "New notification for all users!"
}
```

### Socket Example (Specific Room)
```json
{
  "type": "SOCKET",
  "recipient": "room:user123",
  "message": "Order status updated",
  "metadata": {
    "event": "order-update"
  }
}
```

---

## Complete Postman Collection (Import This)

Save this as `notification-center.postman_collection.json`:

```json
{
  "info": {
    "name": "Notification Center API",
    "description": "Test all notification providers",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Send SMS (SMSGatewayHub)",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "url": {
          "raw": "http://localhost:9090/notifications/api/v1/send",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9090",
          "path": ["notifications", "api", "v1", "send"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\n  \"type\": \"SMS\",\n  \"recipient\": \"919876543210\",\n  \"message\": \"Hello! This is a test SMS from Notification Center.\"\n}"
        }
      }
    },
    {
      "name": "Send Email",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "url": {
          "raw": "http://localhost:9090/notifications/api/v1/send",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9090",
          "path": ["notifications", "api", "v1", "send"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\n  \"type\": \"EMAIL\",\n  \"recipient\": \"user@example.com\",\n  \"subject\": \"Test Email\",\n  \"message\": \"<h1>Welcome!</h1><p>This is a test email.</p>\"\n}"
        }
      }
    },
    {
      "name": "Send Socket Broadcast",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "url": {
          "raw": "http://localhost:9090/notifications/api/v1/send",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9090",
          "path": ["notifications", "api", "v1", "send"]
        },
        "body": {
          "mode": "raw",
          "raw": "{\n  \"type\": \"SOCKET\",\n  \"recipient\": \"broadcast\",\n  \"message\": \"System notification for all users!\"\n}"
        }
      }
    },
    {
      "name": "Send Bulk SMS",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "url": {
          "raw": "http://localhost:9090/notifications/api/v1/send/bulk",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9090",
          "path": ["notifications", "api", "v1", "send", "bulk"]
        },
        "body": {
          "mode": "raw",
          "raw": "[\n  {\n    \"type\": \"SMS\",\n    \"recipient\": \"919876543210\",\n    \"message\": \"Message 1\"\n  },\n  {\n    \"type\": \"SMS\",\n    \"recipient\": \"919876543211\",\n    \"message\": \"Message 2\"\n  }\n]"
        }
      }
    },
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "url": {
          "raw": "http://localhost:9090/notifications/api/v1/health",
          "protocol": "http",
          "host": ["localhost"],
          "port": "9090",
          "path": ["notifications", "api", "v1", "health"]
        }
      }
    }
  ]
}
```

---

## Testing Steps

1. **Start your application**
   ```bash
   # Make sure the application is running on port 9090
   ```

2. **Enable SMS in configuration**
   - Set `notification.sms.enabled: true` in application.yml ✅ (Already done)
   - Configure SMSGatewayHub credentials ✅ (Already configured)

3. **Open Postman**
   - Click "Import" in Postman
   - Paste the JSON collection above
   - Or manually create a request

4. **Send Test Request**
   - Use the "Send SMS (SMSGatewayHub)" request
   - Update the recipient number (must be valid format)
   - Click "Send"

5. **Check Response**
   - Look for `"success": true` in the response
   - Check `providerResponse` for gateway details
   - If error, check `errorMessage` for details

---

## Troubleshooting

### Error: "Connection refused"
- Application is not running
- Check if running on correct port (9090)

### Error: "No provider configured for type: SMS"
- SMS is disabled → Set `notification.sms.enabled: true`
- Restart application after config changes

### Error: "Failed to send SMS"
- Invalid credentials in application.yml
- Check auth-key and client-id are correctly set
- Verify phone number format
- Check network connectivity to SMS gateway

### Provider-specific errors
- Check `providerResponse.rawResponse` for gateway error details
- Verify SMSGatewayHub account has sufficient balance
- Ensure sender-id is registered with the provider

---

## Quick Copy-Paste for Postman

**URL:**
```
http://localhost:9090/notifications/api/v1/send
```

**Method:** POST

**Headers:**
```json
{
  "Content-Type": "application/json"
}
```

**Body (JSON):**
```json
{
  "type": "SMS",
  "recipient": "919876543210",
  "message": "Test message from Notification Center!"
}
```
