package ru.practicum.main.event.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateCommentUserRequestDto {

    @NotBlank
    @NotNull
    private String text;

}
