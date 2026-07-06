package com.berk.libtrack.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BorrowingNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleBorrowing(BorrowingNotAllowedException ex, HttpServletRequest req){
        ErrorResponse body = new ErrorResponse(Instant.now(),
                409, ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ExceptionOther.class)
    public ResponseEntity<ErrorResponse> handleOther(ExceptionOther ex, HttpServletRequest req){
        ErrorResponse body = new ErrorResponse(Instant.now(),
                500, ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status((HttpStatus.INTERNAL_SERVER_ERROR)).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req){
        ErrorResponse body = new ErrorResponse(Instant.now(),
                404, ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex, HttpServletRequest req){
        ErrorResponse body = new ErrorResponse(Instant.now(),
                409, ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageIntegrity(
            HttpMessageNotReadableException ex, HttpServletRequest req){
        ErrorResponse body = new ErrorResponse(Instant.now(),
                400, ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuth(
            AuthorizationFailedException ex, HttpServletRequest req){
        ErrorResponse body = new ErrorResponse(Instant.now(), 401, ex.getMessage(), req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
}
