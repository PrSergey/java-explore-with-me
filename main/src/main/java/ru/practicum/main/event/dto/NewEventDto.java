package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.model.Location;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@Data
@Builder
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private long category;

    @NotBlank
    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private String eventDate;

    @NotNull
    private Location location;

    private Boolean paid;

    private Integer participantLimit;

    
    private Boolean requestModeration;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

}
