package ru.practicum.main.event.dto;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.dto.UserMapper;

@Mapper(uses = {UserMapper.class})
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event toEventFromNewEventDto(NewEventDto newEventDto);

    EventDto toEventDto(Event event);


    EventShortDto toEventShortDto(Event event);
}
