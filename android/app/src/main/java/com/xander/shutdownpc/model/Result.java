package com.xander.shutdownpc.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result<T> {
    private T data;
    private Status status;

    public enum Status {
        SUCCESS, ERROR, LOADING
    }
}
