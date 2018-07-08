package org.notification.dto.sendsms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode
public final class SendSMSInfoDto implements Serializable {

    private static final long serialVersionUID = -4389149313599009705L;

    @JsonProperty("messages")
    private List<SendSMSMessagesInfoDto> messages = new LinkedList<>();

    public List<SendSMSMessagesInfoDto> getMessages() {
        return messages;
    }

    public List<SendSMSMessagesInfoDto> addMessage(SendSMSMessagesInfoDto message) {
        this.messages.add(message);
        return this.messages;
    }
}
