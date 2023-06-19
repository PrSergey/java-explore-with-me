package ru.practicum.main.event.services;

import ru.practicum.main.constant.EventSort;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    List<EventDto> searchEvent(List<Long> users, List<EventState> states, List<Long> categories,
                               String rangeStart, String rangeEnd, int from, int size);

    EventDto updateAdminByEvent(long eventId, UpdateEventAdminRequestDto eventDto);

    EventDto saveEvent(long userId, NewEventDto newEventDto);

    List<EventShortDto> getAllEventByInitiatorId(long userID, int from, int size);

    EventDto getEventById(long userId, long eventId);

    EventDto updateEvent(long userId, long eventId, UpdateEventUserRequestDto updateEvent);

    List<ParticipationRequestDto> getRequestByEvent(long userId, long eventId);

    EventRequestStatusUpdateResultDto updateEventRequest(long userId, long eventId,
                                                         EventRequestStatusUpdateRequestDto requestsByEvent);

    List<EventDto> getEvent(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                            Boolean onlyAvailable, EventSort sort, int from, int size, HttpServletRequest request);

    EventDto getEventById(long eventId, HttpServletRequest request);

}
