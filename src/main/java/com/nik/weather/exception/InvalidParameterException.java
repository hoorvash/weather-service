package com.nik.weather.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidParameterException extends MyException {

    public InvalidParameterException(String message, int errorCode) {
        super(message, errorCode);
    }
}
