package main.exceptions;

public class ConflictTimeException extends RuntimeException {
    public ConflictTimeException(String message) {
        super(message);
    }
}