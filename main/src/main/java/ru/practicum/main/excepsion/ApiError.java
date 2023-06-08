package ru.practicum.main.excepsion;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public class ApiError {

    private final String status;
    private final String message;
    private final String reason;
    private final LocalDateTime time;


    public ApiError(String status, String message, String reason, LocalDateTime time) {
        this.status = status;
        this.message = message;
        this.reason = reason;
        this.time = time;
    }
}