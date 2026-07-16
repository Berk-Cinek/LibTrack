package com.berk.libtrack.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test-endpoint");
    }

    @Test
    void handleBorrowing_returns409() {
        BorrowingNotAllowedException ex = new BorrowingNotAllowedException("Borrowing failed");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBorrowing(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("Borrowing failed");
        assertThat(response.getBody().path()).isEqualTo("/api/test-endpoint");
    }

    @Test
    void handleOther_returns500() {
        ExceptionOther ex = new ExceptionOther("Internal error occurred");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleOther(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().message()).isEqualTo("Internal error occurred");
        assertThat(response.getBody().path()).isEqualTo("/api/test-endpoint");
    }

    @Test
    void handleNotFound_returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource missing");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Resource missing");
        assertThat(response.getBody().path()).isEqualTo("/api/test-endpoint");
    }

    @Test
    void handleMessageIntegrity_returns400() {
        // Mock the exception to avoid its complex constructor (needs an HttpInputMessage).
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMessage()).thenReturn("Malformed JSON");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleMessageIntegrity(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().message()).isEqualTo("Malformed JSON");
        assertThat(response.getBody().path()).isEqualTo("/api/test-endpoint");
    }

    @Test
    void handleAuth_returns401() {
        AuthorizationFailedException ex = new AuthorizationFailedException("Invalid credentials");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAuth(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().message()).isEqualTo("Invalid credentials");
        assertThat(response.getBody().path()).isEqualTo("/api/test-endpoint");
    }

    @Test
    void handleDataIntegrityViolation_returns409() {
        DataIntegrityException ex = new DataIntegrityException("Custom constraint failed");

        ResponseEntity<ErrorResponse> response = exceptionHandler.HandleDataIntegrityViolation(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message()).isEqualTo("Custom constraint failed");
        assertThat(response.getBody().path()).isEqualTo("/api/test-endpoint");
    }

    @Test
    void handleSpringDataIntegrity_returns409WithCustomMessage() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Spring DB constraint failed");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleSpringDataIntegrity(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(409);
        assertThat(response.getBody().message())
                .isEqualTo("This operation conflicts with existing related records.");
        assertThat(response.getBody().path()).isEqualTo("/api/test-endpoint");
    }
}