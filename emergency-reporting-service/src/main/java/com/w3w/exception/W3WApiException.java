package com.w3w.exception;

import lombok.Data;

@Data
public class W3WApiException extends RuntimeException {

    private String errorCode;
    private String errorMessage;

    public W3WApiException(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMessage = errorMsg;
    }
}
