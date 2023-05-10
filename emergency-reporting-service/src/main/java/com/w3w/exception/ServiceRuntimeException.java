package com.w3w.exception;

public class ServiceRuntimeException extends RuntimeException {

    public ServiceRuntimeException() {
        super();
    }

    public ServiceRuntimeException(String msg) {
        super(msg);
    }

}
