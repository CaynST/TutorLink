package com.tutorlink.exception;

import com.tutorlink.model.dto.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    protected ResponseEntity<ApiResponse> handleValidationException(ValidationException ex) {
        ApiResponse body = new ApiResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    protected ResponseEntity<ApiResponse> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        ApiResponse body = new ApiResponse(false, "Acceso denegado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException ex) {
        ApiResponse body = new ApiResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<ApiResponse> handleUnauthorized(UnauthorizedException ex) {
        ApiResponse body = new ApiResponse(false, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        String msg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ApiResponse body = new ApiResponse(false, msg.isEmpty() ? "Invalid request" : msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse> handleAll(Exception ex) {
        ApiResponse body = new ApiResponse(false, "Error interno del servidor");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(OllamaCommunicationException.class)
    protected ResponseEntity<ApiResponse> handleOllamaCommunication(OllamaCommunicationException ex) {
        ApiResponse body = new ApiResponse(false, ex.getMessage() == null || ex.getMessage().isBlank()
                ? "Servicio de LLM no disponible" : ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

}
