package org.notification.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode
@JsonInclude(value = Include.NON_NULL)
public class NotificationResponseDto implements Serializable {

    private static final long serialVersionUID = 883771004602524041L;

    @JsonProperty("referenceNumber")
    private String referenceNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("clientId")
    private String clientId;

    public NotificationResponseDto(
            @JsonProperty("referenceNumber") String referenceNumber,
            @JsonProperty("status") String status,
            @JsonProperty("clientId") String clientId) {
        this.referenceNumber = referenceNumber;
        this.status = status;
        this.clientId = clientId;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getClientId() {
        return clientId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "NotificationResponseDto [referenceNumber=" + referenceNumber + " , status =" + status + " , clientId =" + clientId + "]";
    }
}
