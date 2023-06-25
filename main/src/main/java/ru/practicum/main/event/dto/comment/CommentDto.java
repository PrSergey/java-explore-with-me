package ru.practicum.main.event.dto.comment;

import lombok.Data;
import ru.practicum.main.event.dto.events.EventShortDto;
import ru.practicum.main.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class CommentDto {

    private long id;

    @NotNull
    @NotBlank
    private String text;

    private EventShortDto event;

    private UserShortDto creator;

    private LocalDateTime created;

}
