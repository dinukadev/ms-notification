
### General

ms-notification receives requests to send out notifications to external parties. Emails and SMS are 
supported. The user will be given both a synchronous and asynchronous API to use with the module.

### Sending email attachments

When sending email attachments, the sender needs to make sure that the byte stream is base64 encoded as otherwise ms-notification will throw out an exception and will not process the request. 

As for the MIME type, ms-notification will figure that out internally by using the ***jmimemagic*** library which inspects the bytestream and figures out the content type based on the bit padding on the stream.


# API

When calling any rest api in ms-notification, a custom HTTP header named "Client-Id" needs to be passed in with the given client id for the application.


* Send Email
```json
{
  "toRecipients": [
    "string"
  ],
  "ccRecipients": [
    "string"
  ],
  "bccRecipients": [
    "string"
  ],
  "message": "string",
  "subject": "string",
  "emailAttachments": [
    {
      "encodedByteStream": "string",
      "fileName": "string"
    }
  ]
}
```
- Send SMS

```json
{
  "toNumber": "string",
  "message": "string"
}
```

* Response

```json

{
  "data": {
    "referenceNumber": "string"
  }
}
```

##### POST : /v1/notification/sync/sendEmail

Send a new email synchronously using the details passed in.

- ***request body*** : see above Send Email payload.
- ***response body*** : see above Send Email payload.

##### POST /v1/notification/async/sendEmail
Send a new email asynchronously using the details passed in.

- ***request body*** : see above Send Email payload.
- ***response body*** : see above Send Email payload.

##### POST : /v1/notification/sync/sendSMS

Send a new SMS synchronously using the details passed in.

- ***request body*** : see above Send SMS payload.
- ***response body*** : see above Send SMS payload.

##### POST /v1/notification/async/sendSMS
Send a new SMS asynchronously using the details passed in.

- ***request body*** : see above Send SMS payload.
- ***response body*** : see above Send SMS payload.



