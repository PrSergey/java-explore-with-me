package ru.practicum.main.user.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {

     Long id;

     @NotBlank
     String name;

     @Email
     @NotNull
     String email;

}
