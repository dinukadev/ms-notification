package org.notification.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Dto<T> {

    @JsonProperty("data")
    private T data;

    @JsonCreator
    public Dto(@JsonProperty("data") T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}