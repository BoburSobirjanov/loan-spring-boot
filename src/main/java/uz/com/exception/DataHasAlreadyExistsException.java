package uz.com.exception;

public class DataHasAlreadyExistsException extends RuntimeException {
    public DataHasAlreadyExistsException(String message) {
        super(message);
    }
}
