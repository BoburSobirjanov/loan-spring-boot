package uz.com.exception;

import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

@Getter
public class RequestValidationException extends RuntimeException {
    String message;

    public RequestValidationException(List<ObjectError> allErrors) {
        StringBuilder errorMessage = new StringBuilder();
        for (ObjectError allError : allErrors) {
            errorMessage.append(allError.getDefaultMessage()).append("\n");
        }
        this.message = errorMessage.toString();
    }
}
