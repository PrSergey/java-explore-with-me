package ru.practicum.main.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.user.sevice.UserService;

@Slf4j
@RestController
@RequestMapping("/admin")
public class UserController {

    UserService userService;


}
