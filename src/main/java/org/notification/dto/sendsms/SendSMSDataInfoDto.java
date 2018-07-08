package org.notification.dto.sendsms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode
@ToString
public final class SendSMSDataInfoDto implements Serializable {

    private static final long serialVersionUID = -6843985762052809370L;

    @JsonProperty("messages")
    private List<SendSMSMessagesInfoDto> messages;

    public SendSMSDataInfoDto(@JsonProperty("messages") List<SendSMSMessagesInfoDto> messages) {
        this.messages = messages;
    }

    public List<SendSMSMessagesInfoDto> getMessages() {
        return messages;
    }
}
