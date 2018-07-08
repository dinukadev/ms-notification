package org.notification.dto.sendsms;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode
@ToString
public final class SendSMSMessagesInfoDto implements Serializable {

    private static final long serialVersionUID = -7289523725549643480L;

    @JsonProperty("to")
    private String toNumber;

    @JsonProperty("body")
    private String message;

    @JsonProperty("custom_string")
    private String customString;

    @JsonProperty("status")
    private String status;

    @JsonProperty("from")
    private String from;

    public SendSMSMessagesInfoDto(
            @JsonProperty("to") String toNumber,
            @JsonProperty("body") String message,
            @JsonProperty("custom_string") String customString,
            @JsonProperty("status") String status,
            @JsonProperty("from") String from) {
        this.toNumber = toNumber;
        this.message = message;
        this.customString = customString;
        this.status = status;
        this.from = from;
    }

    public String getToNumber() {
        return toNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getCustomString() {
        return customString;
    }

    public String getStatus() {
        return status;
    }

    public String getFrom() {
        return from;
    }
}
