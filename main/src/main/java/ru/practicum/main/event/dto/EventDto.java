package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventDto {

    private long id;

    private String annotation;

    private Category category;

    private Integer confirmedRequests;

    private LocalDateTime createdOn;

    private String description;

    private String eventDate;

    private UserShortDto initiator;

    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String title;

    private Long views;

}
