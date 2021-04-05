package com.xander.shutdownpc.model.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import okhttp3.ResponseBody;

@Data
@AllArgsConstructor
public class HttpException extends Exception {
    private int code;
    private ResponseBody body;
}
