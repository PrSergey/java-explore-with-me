package ru.practicum.main.event.services;

import ru.practicum.main.constant.EventState;
import ru.practicum.main.event.dto.EventDto;
import ru.practicum.main.event.dto.UpdateEventAdminRequestDto;

import java.util.List;

public interface EventAdminService {

    List<EventDto> searchEvent(List<Long> users, List<EventState> states, List<Long> categories,
                               String rangeStart, String rangeEnd, int from, int size);

    EventDto updateAdminByEvent(long eventId, UpdateEventAdminRequestDto eventDto);

}
