package ru.practicum.main.excepsion;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}