package ru.practicum.main.user.dto;

import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
public class UserDto {

     Long id;

     @NotBlank
     String name;

     @Email
     @NotNull
     String email;

}
