package com.nik.weather.exception;

public class MyException extends Exception {

    protected int errorCode;

    public MyException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
