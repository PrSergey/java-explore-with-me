package ru.practicum.main.event.services;

import ru.practicum.main.constant.EventSort;
import ru.practicum.main.event.dto.events.EventDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventPublicService {

    List<EventDto> getEvent(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                            Boolean onlyAvailable, EventSort sort, int from, int size, HttpServletRequest request);

    EventDto getEventById(long eventId, HttpServletRequest request);

}
