package org.notification.dto.receivesms;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode
@ToString
public class ReceiveSMSResponseInfoDto implements Serializable {

    private static final long serialVersionUID = 8940547746211140713L;

    @JsonProperty("http_code")
    private String httpCode;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("response_msg")
    private String responseMessage;

    @JsonProperty("data")
    private ReceiveSMSDataInfoDto data;

    public ReceiveSMSResponseInfoDto(
            @JsonProperty("http_code") String httpCode,
            @JsonProperty("response_code") String responseCode,
            @JsonProperty("response_msg") String responseMessage,
            @JsonProperty("data") ReceiveSMSDataInfoDto data) {
        this.httpCode = httpCode;
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
        this.data = data;
    }

    public String getHttpCode() {
        return httpCode;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public ReceiveSMSDataInfoDto getData() {
        return data;
    }
}
