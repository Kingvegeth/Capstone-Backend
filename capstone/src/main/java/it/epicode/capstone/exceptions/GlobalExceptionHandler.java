package it.epicode.capstone.exceptions;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("id", ex.idNotFound);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<Object> handleInvalidLoginException(InvalidLoginException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("username", ex.username);
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<Object> handleApiValidationException(ApiValidationException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("errors", ex.errorsList);
        return new ResponseEntity<>(body, ex.status);
    }

    @ExceptionHandler(FileSizeExceededException.class)
    public ResponseEntity<Object> handleFileSizeExceededException(FileSizeExceededException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LastAdminDeletionException.class)
    public ResponseEntity<Object> handleLastAdminDeletionException(LastAdminDeletionException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Object> handleSecurityException(SecurityException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<Object> handleEntityExistsException(EntityExistsException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Gestione delle eccezioni generiche
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "An unexpected error occurred");
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
