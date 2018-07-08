package org.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import org.notification.utils.JsonJodaDateTimeDeserializer;
import org.notification.utils.JsonJodaDateTimeSerializer;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * @author dinuka
 */
@EqualsAndHashCode
@JsonInclude(Include.NON_NULL)
public class IncomingNotificationDto implements Serializable {

    private static final long serialVersionUID = -2940704509338974488L;

    @JsonProperty("clientId")
    private String clientId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("referenceNumber")
    private String referenceNumber;

    @JsonSerialize(using = JsonJodaDateTimeSerializer.class)
    @JsonProperty("receivedDate")
    private DateTime receivedDate;

    public IncomingNotificationDto(
            @JsonProperty("clientId") String clientId,
            @JsonProperty("message") String message,
            @JsonProperty("sender") String sender,
            @JsonProperty("referenceNumber") String referenceNumber,
            @JsonProperty("receivedDate") @JsonDeserialize(using = JsonJodaDateTimeDeserializer.class) DateTime receivedDate) {
        this.clientId = clientId;
        this.message = message;
        this.sender = sender;
        this.referenceNumber = referenceNumber;
        this.receivedDate = receivedDate;
    }

    public String getClientId() {
        return clientId;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public DateTime getReceivedDate() {
        return receivedDate;
    }

    @Override
    public String toString() {
        return "IncomingMailNotificationDto [clientId=" + clientId + ", message=" + message + ", sender=" + sender
                + ", referenceNumber=" + referenceNumber + ", receivedDate=" + receivedDate + "]";
    }

}
