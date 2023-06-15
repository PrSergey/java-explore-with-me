package ru.practicum.main.compilation.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
public class NewCompilationDto {


    List<Long> events;

    Boolean pinned;

    @Size(min = 1, max = 50)
    String title;
}
