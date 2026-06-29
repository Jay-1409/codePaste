# Letterbox API Endpoints

Below is a documentation for all supported endpoints. You can use these end points for your project. 

**Node** We also have documented all end points under `/bruno` you may load this directory into [Bruno](https://www.usebruno.com/) (an api testing tool), and directly test the end points.

## Base URL

```text
http://localhost:8080
```

All endpoints are currently accessible without authentication.

## Endpoint summary

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/paste/system` | Check whether the application is running |
| `POST` | `/paste/addPaste` | Create a paste |
| `GET` | `/paste/pasteExist` | Check whether a paste ID exists |
| `GET` | `/paste/getPaste` | Fetch a paste |
| `GET` | `/paste/protectedPaste` | Check whether a paste is protected |
| `DELETE` | `/paste/deletePaste` | Delete a paste |

## Check system health

```http
GET /paste/system
```

Successful response: `200 OK`

```text
system is healthy
```

## Create a public paste

```http
POST /paste/addPaste
Content-Type: application/json
```

```json
{
  "paste": "Hello from Letterbox",
  "expireAfter": 7,
  "access": true
}
```

Successful response: `200 OK`

```json
{
  "pasteId": "01",
  "paste": "Hello from Letterbox",
  "pastePass": null,
  "access": true,
  "expireAfter": "2026-07-06T10:00:00Z"
}
```

## Create a protected paste

Set `access` to `false` and provide a password.

```http
POST /paste/addPaste
Content-Type: application/json
```

```json
{
  "paste": "This paste is protected",
  "expireAfter": 7,
  "access": false,
  "pastePass": "strong-password"
}
```

### Creation fields

| Field | Required | Rules |
| --- | --- | --- |
| `paste` | Yes | Must not be blank; maximum 524,288 characters |
| `pasteId` | No | Maximum 64 characters; letters, numbers, `_`, and `-` only |
| `expireAfter` | No | Number of days from 1 to 365; defaults to 30 days |
| `access` | No | `true` for public or `false` for protected; defaults to `true` |
| `pastePass` | For protected pastes | Must contain 8 to 72 characters |

If `pasteId` is omitted, Letterbox generates one. A duplicate custom ID returns `409 Conflict`.

The current entity response includes `pastePass`. It is `null` for public pastes and contains the stored BCrypt hash for protected pastes; clients should not depend on this field.

## Check whether a paste exists

```http
GET /paste/pasteExist?pasteId=01
```

Successful response: `200 OK`

```json
true
```

The response is `false` when the ID does not exist. Accessing an expired ID returns `410 Gone` and removes the expired paste.

## Fetch a public paste

```http
GET /paste/getPaste?pasteId=01
```

Successful response: `200 OK`

```json
{
  "pasteId": "01",
  "paste": "Hello from Letterbox",
  "pastePass": null,
  "access": true,
  "expireAfter": "2026-07-06T10:00:00Z"
}
```

## Fetch a protected paste

Pass the original password as a query parameter.

```http
GET /paste/getPaste?pasteId=01&password=strong-password
```

An absent or incorrect password returns `403 Forbidden`.

## Check whether a paste is protected

```http
GET /paste/protectedPaste?pasteId=01
```

Successful response: `200 OK`

```json
true
```

The response is `true` for a protected paste and `false` for a public paste.

## Delete a paste

```http
DELETE /paste/deletePaste?pasteId=01
```

Successful response: `200 OK`

```json
true
```

Paste deletion currently requires only the paste ID. It does not require the paste password.

## Error responses

| Status | Meaning |
| --- | --- |
| `400 Bad Request` | Invalid request body, query parameter, or paste ID |
| `403 Forbidden` | Missing or incorrect paste password |
| `404 Not Found` | Paste ID does not exist |
| `409 Conflict` | Requested custom paste ID already exists |
| `410 Gone` | Paste has expired |

Validation errors return a field-to-message object:

```json
{
  "paste": "Paste content is required",
  "expireAfter": "Expiration must be greater than zero days"
}
```
