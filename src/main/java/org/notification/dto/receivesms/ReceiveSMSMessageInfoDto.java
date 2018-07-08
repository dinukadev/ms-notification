package org.notification.dto.receivesms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode
public class ReceiveSMSMessageInfoDto implements Serializable {

    private static final long serialVersionUID = 264866883015184034L;

    @JsonProperty("from")
    private String from;

    @JsonProperty("body")
    private String body;

    @JsonProperty("custom_string")
    private String referenceNumber;

    @JsonProperty("timestamp")
    private String timeStamp;

    public ReceiveSMSMessageInfoDto(
            @JsonProperty("from") String from,
            @JsonProperty("body") String body,
            @JsonProperty("custom_string") String referenceNumber,
            @JsonProperty("timestamp") String timeStamp) {
        this.from = from;
        this.body = body;
        this.referenceNumber = referenceNumber;
        this.timeStamp = timeStamp;
    }

    public String getFrom() {
        return from;
    }

    public String getBody() {
        return body;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
