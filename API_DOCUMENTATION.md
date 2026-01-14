# API Documentation

## Base URL

```
http://localhost:8090/notifications
```

## Endpoints

### 1. Send Notification (Sync)

Send a single notification synchronously.

**Endpoint:** `POST /api/v1/send`

**Request Body:**
```json
{
  "type": "WHATSAPP",
  "recipient": "+1234567890",
  "message": "Hello from Notification Center!"
}
```

**Response:**
```json
{
  "success": true,
  "messageId": "wamid.HBgNMTIzNDU2Nzg5MAVCABIYFjNFQjBDMEE4RkQxMjM0NUFCNAA=",
  "status": "SENT",
  "timestamp": "2026-01-07T16:45:00",
  "providerResponse": {}
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8090/notifications/api/v1/send \
  -H "Content-Type: application/json" \
  -d '{
    "type": "WHATSAPP",
    "recipient": "+1234567890",
    "message": "Hello!"
  }'
```

### 2. Send Notification (Async)

Send a notification asynchronously.

**Endpoint:** `POST /api/v1/send/async`

**Request Body:** Same as sync endpoint

**Response:** Same as sync endpoint (returned asynchronously)

### 3. Send Bulk Notifications

Send multiple notifications at once.

**Endpoint:** `POST /api/v1/send/bulk`

**Request Body:**
```json
[
  {
    "type": "WHATSAPP",
    "recipient": "+1234567890",
    "message": "Message 1"
  },
  {
    "type": "WHATSAPP",
    "recipient": "+0987654321",
    "message": "Message 2"
  }
]
```

**Response:**
```json
[
  {
    "success": true,
    "messageId": "msg-id-1",
    "status": "SENT"
  },
  {
    "success": true,
    "messageId": "msg-id-2",
    "status": "SENT"
  }
]
```

### 4. Health Check

Check if the service is running.

**Endpoint:** `GET /api/v1/health`

**Response:**
```
Notification Center is running
```

## Request Parameters

### NotificationRequest

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | enum | Yes | WHATSAPP, SMS, EMAIL, SOCKET |
| recipient | string | Yes | Phone number, email, or user ID |
| message | string | Yes* | Message content (*not required for templates) |
| subject | string | No | Email subject |
| templateId | string | No | Template identifier |
| templateParameters | map | No | Template variable values |
| media | object | No | Media attachment |
| priority | enum | No | LOW, NORMAL, HIGH, URGENT |
| metadata | map | No | Additional custom data |

### MediaAttachment

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| url | string | Yes | Media file URL |
| type | string | Yes | image, video, document, audio |
| filename | string | No | File name |
| caption | string | No | Media caption |

## WhatsApp-Specific Examples

### Send Text Message

```json
{
  "type": "WHATSAPP",
  "recipient": "1234567890",
  "message": "Hello from Notification Center!"
}
```

### Send Template Message

```json
{
  "type": "WHATSAPP",
  "recipient": "1234567890",
  "templateId": "welcome_message",
  "templateParameters": {
    "name": "John Doe",
    "code": "ABC123"
  }
}
```

### Send Image

```json
{
  "type": "WHATSAPP",
  "recipient": "1234567890",
  "media": {
    "url": "https://example.com/image.jpg",
    "type": "image",
    "caption": "Check this out!"
  }
}
```

### Send Document

```json
{
  "type": "WHATSAPP",
  "recipient": "1234567890",
  "media": {
    "url": "https://example.com/document.pdf",
    "type": "document",
    "filename": "invoice.pdf"
  }
}
```

## Error Responses

### Failed Notification

```json
{
  "success": false,
  "status": "FAILED",
  "errorMessage": "Invalid phone number format",
  "timestamp": "2026-01-07T16:45:00"
}
```

### Provider Not Configured

```json
{
  "success": false,
  "status": "FAILED",
  "errorMessage": "No provider configured for type: SMS",
  "timestamp": "2026-01-07T16:45:00"
}
```

## Postman Collection

Import this JSON to test the API in Postman:

```json
{
  "info": {
    "name": "Notification Center API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Send Notification",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": "http://localhost:8090/notifications/api/v1/send",
        "body": {
          "mode": "raw",
          "raw": "{\"type\":\"WHATSAPP\",\"recipient\":\"+1234567890\",\"message\":\"Test message\"}"
        }
      }
    }
  ]
}
```
