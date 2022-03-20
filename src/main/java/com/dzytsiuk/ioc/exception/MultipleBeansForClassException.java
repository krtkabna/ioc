package com.dzytsiuk.ioc.exception;


public class MultipleBeansForClassException extends RuntimeException {
    public MultipleBeansForClassException(String message) {
        super(message);
    }

}
