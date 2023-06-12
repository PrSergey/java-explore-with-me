package ru.practicum.main.user.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {

     Long id;

     @NotBlank
     @Size(min = 2, max = 250)
     String name;

     @Email
     @NotNull
     @Size(min = 6, max = 254)
     String email;

}
