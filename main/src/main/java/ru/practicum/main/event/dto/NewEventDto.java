package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@Builder
public class NewEventDto {

    @NotBlank
    private String annotation;

    @NotNull
    private Category category;

    @NotBlank
    private String description;

    @NotNull
    @Future
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank
    private String title;

}
