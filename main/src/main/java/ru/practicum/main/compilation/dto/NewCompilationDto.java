package ru.practicum.main.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@ToString
public class NewCompilationDto {

    List<Long> events;

    Boolean pinned;

    @NotBlank
    String title;
}
