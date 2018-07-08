package org.notification.dto.receivesms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author dinuka
 */

@EqualsAndHashCode
@JsonInclude(Include.NON_NULL)
public class MarkSMSReadRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("date_before")
    private String timeStamp;

    public MarkSMSReadRequestDto(@JsonProperty("date_before") String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

}
