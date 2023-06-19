package ru.practicum.main.category.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Data
@Builder
public class CategoryDto {

    private long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;

}
