package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.user.dto.UserShortDto;
import ru.practicum.main.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {

    private String annotation;

    private Category category;

    private Integer confirmedRequests;

    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;

}
