package com.github.misterchangray.service.jsexcutor.exception;

public class FMZException extends RuntimeException {
    private String msg;


    public FMZException(String message) {
        super(message);
        this.msg = message;
    }
}
