package org.notification.dto.receivesms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode
public class ReceiveSMSDataInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("data")
    private List<ReceiveSMSMessageInfoDto> messages;

    public ReceiveSMSDataInfoDto(@JsonProperty("data") List<ReceiveSMSMessageInfoDto> messages) {
        this.messages = messages;
    }

    public List<ReceiveSMSMessageInfoDto> getMessages() {
        return messages;
    }
}
