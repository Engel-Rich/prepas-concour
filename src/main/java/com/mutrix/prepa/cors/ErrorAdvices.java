package com.mutrix.prepa.cors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice()
public class ErrorAdvices {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseFormat<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        ex.getBindingResult().getFieldErrors().forEach(System.out::println);

        final String error = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        return ResponseEntity.badRequest().body(ApiResponseFormat.fromError(error));
    }

    // Gestion des erreurs de type HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseFormat<String>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            WebRequest request
    ) {
        return ResponseEntity.badRequest().body(ApiResponseFormat.fromError("Le corps de la requête est manquant ou mal formaté."));
    }

 // Gestion des erreurs de type HttpMessageNotReadableException
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseFormat<String>> handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request
    ) {
        return ResponseEntity.status(404).body(ApiResponseFormat.fromError(ex.getMessage()));
    }
 // Gestion des erreurs de type HttpMessageNotReadableException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseFormat<String>> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request
    ) {
        return ResponseEntity.status(401).body(ApiResponseFormat.fromError(ex.getMessage()));
    }

//     @ExceptionHandler(ExpiredJwtException.class)
//     public ResponseEntity<?> handlerExpireTokenException() {
//     return ResponseEntity.status(401).body(ex.getMessage());
//     }

    // Gestion des autres exceptions générales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseFormat<String>> handleGlobalExceptions(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(ApiResponseFormat.fromError(ex.getMessage()));
    }
}
