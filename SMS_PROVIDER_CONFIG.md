# SMS Provider Configuration Examples

This document provides detailed configuration examples for all supported SMS providers.

## Supported Providers

1. **MSG91** - Popular Indian SMS gateway
2. **24x7SMS** - Enterprise SMS service
3. **SMSGatewayHub** - Multi-channel messaging platform
4. **SMSEveryone** - Bulk SMS service
5. **Twilio** - Global cloud communications platform

---

## Configuration Instructions

### 1. MSG91

```yaml
notification:
  sms:
    enabled: true
    provider: MSG91
    url: https://api.msg91.com/api/sendhttp.php
    params: authkey={authkey}&mobiles={mobiles}&message={message}&sender={sender}&route={route}&DLT_TE_ID={dltTemplateId}
    auth-key: your-msg91-auth-key
    sender-id: TESTIN
    dlt-entity-id: 1234567890123456789
    dlt-template-id: 1234567890123456789
    route: 4  # 1=promotional, 4=transactional
    max-characters-per-sms: 160
```

**Parameter Mapping:**
- `{authkey}` → Your MSG91 API authentication key
- `{mobiles}` → Recipient phone number (e.g., 919876543210)
- `{message}` → SMS message content (URL encoded)
- `{sender}` → Sender ID (6 characters, alphanumeric)
- `{route}` → Message route (4 for transactional)
- `{dltTemplateId}` → DLT Template ID for regulatory compliance

---

### 2. 24x7SMS

```yaml
notification:
  sms:
    enabled: true
    provider: 24X7SMS
    url: https://smsapi.24x7sms.com/api_2.0/SendSMS.aspx
    params: APIKEY={authkey}&MobileNo={mobiles}&SenderID={sender}&Message={message}&ServiceName=TRANSACTIONAL
    auth-key: your-24x7sms-api-key
    sender-id: TESTIN
    route: TRANSACTIONAL
    max-characters-per-sms: 160
```

**Parameter Mapping:**
- `APIKEY={authkey}` → Your 24x7SMS API key
- `MobileNo={mobiles}` → Recipient mobile number
- `SenderID={sender}` → Registered sender ID
- `Message={message}` → SMS text (URL encoded)
- `ServiceName` → TRANSACTIONAL or PROMOTIONAL

**Note:** 24x7SMS uses different parameter names (APIKEY, MobileNo, SenderID)

---

### 3. SMSGatewayHub (VERIFIED ✅)

**URL:** `https://www.smsgatewayhub.com/api/mt/SendSMS`
**Method:** GET

**Configuration in application.yml:**
```yaml
  sms:
    enabled: true
    provider: SMSGATEWAYHUB
    url: https://www.smsgatewayhub.com/api/mt/SendSMS
    params: APIKey={apikey}&senderid={senderid}&channel={channel}&route={route}&number={number}&text={text}
    auth-key: your-api-key
    sender-id: SENDER
    channel: 2  # 1=Promotional, 2=Transactional, OTP=OTP
    route: your-route-id
    max-characters-per-sms: 160
```
**Notes:** 
- The API endpoint is case-sensitive: `SendSMS` (uppercase)
- Requires `channel` parameter (Promotional=1, Transactional=2)
- Uses `APIKey` instead of username/password
- `auth-key` property maps to `{apikey}` template variable
- `sender-id` property maps to `{senderid}` template variable
- `channel` property maps to `{channel}` template variable
- `route` property maps to `{route}` template variable
- `{number}` → Recipient mobile number
- `{text}` → SMS content (URL encoded)

---

### 4. SMSEveryone

```yaml
notification:
  sms:
    enabled: true
    provider: SMSEVERYONE
    url: https://smseveryone.com/api/campaign
    params: api_key={authkey}&sender_id={sender}&destination={mobiles}&message={message}&type={route}
    auth-key: your-smseveryone-api-key
    sender-id: TESTIN
    route: transactional
    max-characters-per-sms: 160
```

**Parameter Mapping:**
- `api_key={authkey}` → Your SMSEveryone API key
- `sender_id={sender}` → Registered sender ID
- `destination={mobiles}` → Recipient mobile number
- `message={message}` → SMS text (URL encoded)
- `type={route}` → transactional or promotional

---

### 5. Twilio

```yaml
notification:
  sms:
    enabled: true
    provider: TWILIO
    url: https://api.twilio.com/2010-04-01/Accounts/{accountSid}/Messages.json
    params: From={sender}&To={mobiles}&Body={message}
    auth-key: your-twilio-auth-token
    client-id: ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx  # Your Twilio Account SID
    sender-id: +1234567890  # Your Twilio phone number with country code
    max-characters-per-sms: 160
```

**Parameter Mapping:**
- URL contains `{accountSid}` → Replaced by `client-id` config value
- `From={sender}` → Your Twilio phone number (must be E.164 format: +1234567890)
- `To={mobiles}` → Recipient phone number (E.164 format)
- `Body={message}` → SMS text (URL encoded)

**Special Notes:**
- Twilio uses **HTTP Basic Authentication**: Username = Account SID (stored in `client-id`), Password = Auth Token (stored in `auth-key`)
- Phone numbers must be in **E.164 format**: `+[country code][number]` (e.g., `+919876543210`, `+12025551234`)
- The `{accountSid}` in the URL path is automatically replaced with the value from `client-id` field
- Twilio typically uses POST requests, but this configuration adapts it for GET with query parameters

**Getting Twilio Credentials:**
1. Sign up at https://www.twilio.com
2. Get your **Account SID** from the Twilio Console (starts with "AC")
3. Get your **Auth Token** from the Twilio Console
4. Purchase a Twilio phone number (must be in E.164 format)

**Example:**
```yaml
client-id: AC1234567890abcdef1234567890abcdef  # Account SID
auth-key: your32characterauthtokenhere123456
sender-id: +12025551234  # Must match your Twilio number
```

---

## Template Parameter Reference

All providers support these template parameters that get replaced at runtime:

| Parameter | Description | Example |
|-----------|-------------|---------|
| `{authkey}` | API authentication key | Maps to `auth-key` config |
| `{mobiles}` | Recipient phone number | From `request.recipient` |
| `{message}` | SMS message content | From `request.message` (URL encoded) |
| `{sender}` | Sender ID | Maps to `sender-id` config |
| `{route}` | Message route/type | Maps to `route` config |
| `{dltTemplateId}` | DLT template ID | From metadata or config |
| `{dltEntityId}` | DLT entity ID | Maps to `dlt-entity-id` config |
| `{clientId}` | Client ID | Maps to `client-id` config |
| `{accountSid}` | Account SID (Twilio) | Maps to `client-id` config |

---

## Usage Example

After configuring your preferred provider:

```java
NotificationRequest request = NotificationRequest.builder()
    .type("SMS")
    .recipient("919876543210")
    .message("Your OTP is 123456. Valid for 5 minutes.")
    .metadata(Map.of("dltTemplateId", "1234567890123"))  // Optional override
    .build();

NotificationResponse response = notificationService.send(request);
```

---

## Switching Providers

To switch between providers, simply:

1. Comment out the current provider configuration
2. Uncomment the desired provider's configuration block
3. Fill in the credentials
4. Change `enabled: true`
5. Restart the application

The SmsProvider implementation automatically uses the configured URL and parameters.
