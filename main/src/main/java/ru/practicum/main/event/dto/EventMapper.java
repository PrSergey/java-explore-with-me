package ru.practicum.main.event.dto;


import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.dto.UserMapper;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring", uses = {UserMapper.class})
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Event toEventFromNewEventDto(NewEventDto newEventDto);

    EventDto toEventDto(Event event);


    EventShortDto toEventShortDto(Event event);

    void update(UpdateEventUserRequest updateEvent, @MappingTarget Event event);
}
