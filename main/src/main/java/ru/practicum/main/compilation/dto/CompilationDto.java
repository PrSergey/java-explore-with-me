package ru.practicum.main.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CompilationDto {

    Integer id;

    List<EventShortDto> events;

    Boolean pinned;

    String title;
}
