package org.notification.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author dinuka
 */
@EqualsAndHashCode
@JsonInclude(Include.NON_NULL)
public class SMSNotificationRequestDto implements Serializable {

    private static final long serialVersionUID = 299545883041365829L;

    @JsonProperty("toNumber")
    @NotNull(message = "recipient number cannot null.")
    private String toNumber;

    @JsonProperty("message")
    @NotNull(message = "Message cannot be null.")
    private String message;

    public SMSNotificationRequestDto(@JsonProperty("toNumber") String toNumber,
                                     @JsonProperty("message") String message) {
        this.toNumber = toNumber;
        this.message = message;
    }

    public String getToNumber() {
        return toNumber;
    }

    public String getMessage() {
        return message;
    }

}
