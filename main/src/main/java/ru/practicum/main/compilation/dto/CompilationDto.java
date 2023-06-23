package ru.practicum.main.compilation.dto;

import lombok.*;
import ru.practicum.main.event.dto.events.EventShortDto;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CompilationDto {

    Long id;

    List<EventShortDto> events;

    Boolean pinned;

    String title;
}
