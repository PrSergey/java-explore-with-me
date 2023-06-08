package ru.practicum.main.request.dto;

import lombok.Data;
import ru.practicum.main.constant.RequestStatus;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {

    private long id;

    private LocalDateTime created;

    private long event;

    private long requester;

    private RequestStatus status;

}
