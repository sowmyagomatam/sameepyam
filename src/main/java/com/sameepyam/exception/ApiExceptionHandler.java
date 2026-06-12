package com.sameepyam.exception;

import com.sameepyam.dto.RiskLevel;
import com.sameepyam.dto.ScamVerdict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(SameepyamException.class)
    ResponseEntity<Map<String, String>> handleException(SameepyamException e) {
        log.warn("Handled app failure ", e);
        return ResponseEntity.status(503).body(Map.of("error", e.getUserMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    ResponseEntity<Map<String, String>> handleBadRequest(Exception e) {
        log.warn("Bad request", e);
        return ResponseEntity.badRequest()
                .body(Map.of("error", "I couldn't read that message. Please send it as plain text."));
    }


}
