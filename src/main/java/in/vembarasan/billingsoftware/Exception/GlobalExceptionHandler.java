package in.vembarasan.billingsoftware.Exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidFilterException.class)
    public ResponseEntity<?> handleInvalidFilter(InvalidFilterException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("error", ex.getMessage())
        );
    }



}
