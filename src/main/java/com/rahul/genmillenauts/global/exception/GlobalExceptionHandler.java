package com.rahul.genmillenauts.global.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 1. Handle @Valid validation errors (bad request 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("success", false);
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", errors.size() == 1 ? errors.get(0) : errors);
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ✅ 2. Handle ResponseStatusException (custom business exceptions like 400, 401, 409)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("success", false);
        body.put("status", ex.getStatusCode().value());
        body.put("message", ex.getReason());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    // ✅ 3. Handle RuntimeExceptions (covers wrapped 409 or 400)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Handle wrapped ResponseStatusException messages
        if (message != null && message.startsWith("409 CONFLICT")) {
            status = HttpStatus.CONFLICT;
            message = message.substring(message.indexOf("\"") + 1, message.lastIndexOf("\""));
        } else if (message != null && message.startsWith("400 BAD_REQUEST")) {
            status = HttpStatus.BAD_REQUEST;
            message = message.substring(message.indexOf("\"") + 1, message.lastIndexOf("\""));
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("success", false);
        body.put("status", status.value());
        body.put("message", message);
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, status);
    }

    // ✅ 4. Catch-all fallback for any unhandled exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("success", false);
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false));

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
