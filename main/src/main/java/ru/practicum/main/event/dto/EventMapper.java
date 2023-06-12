package ru.practicum.main.event.dto;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.main.category.dto.CategoryMapper;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class})
public interface EventMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", expression = "java(makeDateFromString(newEventDto.getEventDate()))")
    Event toEventFromNewEventDto(NewEventDto newEventDto);


    @Mapping(target = "eventDate", expression = "java(makeDateToString(event.getEventDate()))")
    EventDto toEventDto(Event event);


    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    void updateEvent(UpdateEventUserRequestDto updateEvent, @MappingTarget Event event);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", ignore = true)
    void updateAdmin(UpdateEventAdminRequestDto updateEvent, @MappingTarget Event event);

    default String makeDateToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    default LocalDateTime makeDateFromString(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTime, formatter);
    }
}
