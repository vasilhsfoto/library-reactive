package com.vassilis.library.reactive.exception;

import org.springframework.http.HttpStatus;

public class WebAppException extends RuntimeException {
    public final HttpStatus httpStatus;

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public WebAppException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
