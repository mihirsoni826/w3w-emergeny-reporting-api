package com.w3w.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super();
    }

    public BadRequestException(String msg) {
        super(msg);
    }
}
