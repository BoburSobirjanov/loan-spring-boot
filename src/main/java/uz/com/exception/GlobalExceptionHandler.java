package uz.com.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import uz.com.exception.DataHasAlreadyExistsException;
import uz.com.exception.DataNotAcceptableException;
import uz.com.exception.DataNotFoundException;
import uz.com.model.dto.response.GeneralResponse;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DataHasAlreadyExistsException.class)
    public ResponseEntity<GeneralResponse<String>> dataHasAlreadyExistsException(DataHasAlreadyExistsException e){
        return ResponseEntity.status(400).body(GeneralResponse.error(e.getMessage()));
    }

    @ExceptionHandler(value = DataNotFoundException.class)
    public ResponseEntity<GeneralResponse<String>> dataNotFoundException(DataNotFoundException e){
        return ResponseEntity.status(404).body(GeneralResponse.error(e.getMessage()));
    }


    @ExceptionHandler(value = DataNotAcceptableException.class)
    public ResponseEntity<GeneralResponse<String>> dataNotAcceptableException(DataNotAcceptableException e){
        return ResponseEntity.status(406).body(GeneralResponse.error(e.getMessage()));
    }

}
