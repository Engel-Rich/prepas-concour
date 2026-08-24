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

    /**
     * Appareil non lié au compte. Le corps porte le code métier
     * {@code DEVICE_MISMATCH} que le mobile teste pour se déconnecter.
     */
    @ExceptionHandler(com.mutrix.prepa.infrastructure.security.DeviceMismatchException.class)
    public ResponseEntity<java.util.Map<String, Object>> handleDeviceMismatch(
            com.mutrix.prepa.infrastructure.security.DeviceMismatchException ex) {
        java.util.Map<String, Object> body = new java.util.LinkedHashMap<>();
        body.put("statusCode", 409);
        body.put("success", false);
        body.put("error", ex.getMessage());
        body.put("code", com.mutrix.prepa.infrastructure.security.DeviceMismatchException.CODE);
        body.put("data", null);
        return ResponseEntity.status(409).body(body);
    }

    /**
     * Conflit métier : l'état courant interdit l'opération demandée
     * (code déjà consommé, souscription déjà active, session fermée…).
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseFormat<String>> handleIllegalStateException(
            IllegalStateException ex,
            WebRequest request
    ) {
        return ResponseEntity.status(409).body(ApiResponseFormat.fromError(ex.getMessage(), 409));
    }

    // Gestion des autres exceptions générales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseFormat<String>> handleGlobalExceptions(Exception ex, WebRequest request) {
        return ResponseEntity.badRequest().body(ApiResponseFormat.fromError(ex.getMessage()));
    }
}
