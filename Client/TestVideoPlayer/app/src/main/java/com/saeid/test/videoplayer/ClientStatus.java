package com.saeid.test.videoplayer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Enumeration;

/**
 * Created by Diamond on 12/6/15.
 */
public class ClientStatus {

    public enum Status {
        Preparing,
        Prepared,
        Buffering,
        Loading,
        Loaded,
        Playing,
        Finished,
        Error
    };

    @JsonProperty
    private Status status;

    @JsonProperty
    private String value;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
