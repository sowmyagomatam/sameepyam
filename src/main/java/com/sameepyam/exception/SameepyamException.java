package com.sameepyam.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class SameepyamException extends RuntimeException{
    private final HttpStatus status;
    private final String userMessage;

    public SameepyamException(HttpStatus status, String userMessage, Throwable cause){
        super(userMessage, cause);
        this.status = status;
        this.userMessage = userMessage;
    }
}
