package ru.practicum.main.request.services;

import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestPrivateService {

    ParticipationRequestDto save(long userId, long eventId);

    List<ParticipationRequestDto> getRequestByUser(long userID);

    ParticipationRequestDto cancelRequestByUser(long userId, long requestId);

}
