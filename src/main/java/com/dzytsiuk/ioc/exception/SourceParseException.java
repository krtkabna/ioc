package com.dzytsiuk.ioc.exception;


public class SourceParseException extends RuntimeException {

    public SourceParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceParseException(String message) {
        super(message);
    }
}
