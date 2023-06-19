package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventShortDto {

    private LocalDateTime eventDate;

    private String annotation;

    private Category category;

    private Integer confirmedRequests;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;



}
