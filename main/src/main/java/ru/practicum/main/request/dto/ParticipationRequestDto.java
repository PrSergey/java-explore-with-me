package ru.practicum.main.request.dto;

import lombok.Data;
import ru.practicum.main.constant.RequestStatus;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {

    private long id;

    private LocalDateTime created;

    private long event;

    private long requester;

    private RequestStatus status;

}
