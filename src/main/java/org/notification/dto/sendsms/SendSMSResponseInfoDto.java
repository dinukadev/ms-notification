package org.notification.dto.sendsms;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonInclude(Include.NON_NULL)
public final class SendSMSResponseInfoDto implements Serializable {

    private static final long serialVersionUID = 5257081883115623831L;

    @JsonProperty("http_code")
    private String httpCode;

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("response_msg")
    private String responseMessage;

    @JsonProperty("data")
    private SendSMSDataInfoDto data;

    public SendSMSResponseInfoDto() {
    }

    @JsonCreator
    public SendSMSResponseInfoDto(
            @JsonProperty("http_code") String httpCode,
            @JsonProperty("response_code") String responseCode,
            @JsonProperty("response_msg") String responseMessage,
            @JsonProperty("data") SendSMSDataInfoDto data) {
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

    public SendSMSDataInfoDto getData() {
        return data;
    }

    @Override
    public String toString() {
        return "SendSMSResponseInfoDto [httpCode=" + httpCode + ", responseCode=" + responseCode + ", responseMessage="
                + responseMessage + ", data=" + data + "]";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(httpCode).append(responseCode).append(responseMessage)
                .append(responseMessage).append(data).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SendSMSResponseInfoDto other = (SendSMSResponseInfoDto) obj;
        return new EqualsBuilder().append(httpCode, other.httpCode).append(responseCode, other.responseCode)
                .append(responseMessage, other.responseMessage).append(data, other.data).isEquals();
    }

}
