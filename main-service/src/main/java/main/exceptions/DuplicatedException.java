package main.exceptions;

public class DuplicatedException extends RuntimeException {
    public DuplicatedException(final String message) {
        super(message);
    }
}