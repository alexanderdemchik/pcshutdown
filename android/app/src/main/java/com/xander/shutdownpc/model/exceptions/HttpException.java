package com.xander.shutdownpc.model.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import okhttp3.ResponseBody;

@Getter
@Setter
@AllArgsConstructor
public class HttpException extends Exception {
    private int code;
    private ResponseBody body;
}
