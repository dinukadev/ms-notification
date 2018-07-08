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
public class EmailAttachmentInfoDto implements Serializable {

    private static final long serialVersionUID = -4925875377304942129L;

    @JsonProperty(value = "encodedByteStream", required = true)
    private String encodedByteStream;

    @NotNull
    @JsonProperty(value = "fileName", required = true)
    private String fileName;

    public EmailAttachmentInfoDto(@JsonProperty("encodedByteStream") String encodedByteStream,
                                  @JsonProperty("fileName") String fileName) {
        this.encodedByteStream = encodedByteStream;
        this.fileName = fileName;
    }

    public String getEncodedByteStream() {
        return encodedByteStream;
    }

    public String getFileName() {
        return fileName;
    }

}
