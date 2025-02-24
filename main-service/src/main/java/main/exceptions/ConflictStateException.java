package main.exceptions;

public class ConflictStateException extends RuntimeException {
    public ConflictStateException(String message) {
        super(message);
    }
}