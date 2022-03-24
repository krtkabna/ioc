package com.dzytsiuk.ioc.exception;

public class DependencyInjectionException extends RuntimeException {
    public DependencyInjectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
