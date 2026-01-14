# SMS Provider API Documentation Summary

Based on official API documentation read on 2026-01-14

## 1. SMSGatewayHub (VERIFIED ✅)

**Documentation:** https://www.smsgatewayhub.com/https-api

**API Type:** HTTP GET

**Single SMS Endpoint:**
```
https://www.smsgatewayhub.com/api/mt/SendSms
```

**Required Parameters:**
- `APIKey` - API authentication key
- `senderid` - Sender ID (6 characters, alphanumeric)
- `channel` - Message channel:
  - `1` = Promotional
  - `2` = Transactional  
  - `OTP` = OTP messages
- `route` - Route ID (get from panel)
- `number` - Recipient mobile number(s), comma-separated
- `text` - SMS message content
- `DCS` - Data coding (0=normal, 8=unicode)
- `flashsms` - Flash message (0=normal, 1=flash)

**Example URL:**
```
https://www.smsgatewayhub.com/api/mt/SendSms?APIKey={apikey}&senderid={senderid}&channel={channel}&route={route}&number={number}&text={text}
```

**Response Format:** JSON
```json
{
  "ErrorCode": "000",
  "ErrorMessage": "Success",  
  "JobId": "20047",
  "MessageData": [{
    "Number": "91989xxxxxxx",
    "MessageId": "mvHdpSyS7UOs9hjxixQLvw"
  }]
}
```

---

## 2. MSG91 (VERIFIED ✅)

**Documentation:** https://docs.msg91.com/sms

**API Type:** HTTP GET

**Send SMS Endpoint:**
```
https://api.msg91.com/api/v2/sendsms
```

**Required Parameters:**
- `authkey` - Authentication key
- `mobiles` - Recipient mobile numbers (comma-separated with country code)
- `message` - SMS message content
- `sender` - Sender ID
- `route` - Message route:
  - `1` = Promotional
  - `4` = Transactional

**Optional Parameters:**
- `country` - Country code (0=international, 1=USA, 91=India)
- `flash` - Flash SMS (0/1)
- `unicode` - Unicode message (0/1)
- `scheduledatetime` - Schedule time (Y-m-d h:i:s format)
- `response` - Response format (json/xml)
- `campaign` - Campaign name

**Example URL:**
```
https://api.msg91.com/api/v2/sendsms?authkey={authkey}&mobiles={mobiles}&message={message}&sender={sender}&route={route}
```

**Response Format:** JSON/XML/String

---

## 3. SMSEveryone (VERIFIED ✅)

**Documentation:** https://www.smseveryone.com.au/restapi

**API Type:** REST API (POST with JSON)

**Send CampaignEndpoint:**
```
POST https://smseveryone.com/api/campaign
```

**Authentication:** 
- Basic Auth or API Key in header
- Header format: `Authorization: Basic {base64(username:password)}`

**JSON Body Structure:**
```json
{
  "Message": "Your message text",
  "Originator": "61400190499",
  "Destinations": ["61400123456", "61400123999"],
  "Action": "create"
}
```

**Optional Fields:**
- `Reference` - Your reference ID
- `TimeScheduled` - Schedule time (YYYYMMDDHHmm format, e.g., "202008301300")
- `CrmIds` - Array of list IDs to send to

**Character Limits:**
- Normal SMS: 160 chars (1 SMS), 306 chars (2 SMS), etc.
- Unicode SMS: 70 chars (1 SMS), 134 chars (2 SMS), etc.

**Success Response:**
```json
{
  "Code": 0,
  "CampaignId": 11967222,
  "Messages": 1,
  "Segments": 1,
  "Credits": 1
}
```

---

## 4. 24x7SMS (PENDING ⏳)

**Documentation:** Waiting for user to provide

---

## Implementation Notes

### SMSGatewayHub
- Uses GET method
- Parameters in query string
- APIKey for authentication
- Requires channel parameter (different from route)

### MSG91
- Uses GET method
- Parameters in query string
- authkey for authentication
- Simple parameter structure

### SMSEveryone
- **Different from others** - Uses POST with JSON body
- Not compatible with GET-based URL template approach
- Requires REST client for POST requests
- Uses Basic Auth

### Current Implementation Status
- ✅ SmsConfig updated with user/password/channel fields
- ✅ SmsProvider supports multiple parameter name variations
- ⚠️ SMSEveryone requires different implementation (POST + JSON)
- ⏳ Waiting for 24x7SMS API documentation

### Next Steps
1. Get 24x7SMS API details from user
2. Consider creating separate REST provider for SMSEveryone
3. Update application.yml with verified configurations
4. Test each provider individually
