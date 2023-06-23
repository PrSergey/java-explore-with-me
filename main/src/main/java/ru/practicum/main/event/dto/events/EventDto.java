package ru.practicum.main.event.dto.events;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.constant.EventState;
import ru.practicum.main.event.model.Comment;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventDto {

    private LocalDateTime createdOn;

    private String description;

    private String eventDate;

    private Location location;

    private Integer participantLimit;

    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String annotation;

    private Category category;

    private Integer confirmedRequests;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;

    private List<Comment> comment;

}
