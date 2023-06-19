package ru.practicum.stats.excepsion;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}