package ru.practicum.main.user.services.impl;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.srorage.CategoryRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserMapper;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.services.UserAdminService;
import ru.practicum.main.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserAdminServiceImp implements UserAdminService {

    UserRepository userRepository;

    CategoryRepository categoryRepository;

    UserMapper userMapper;

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = userMapper.fromUserDto(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getUsers(List<Long> usersId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<User> users;
        if (usersId == null) {
            users = userRepository.findAll(pageRequest).stream().collect(Collectors.toList());
        } else {
            users = userRepository.findAllByIdIn(usersId, pageRequest);
        }
        return users.stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ExistenceException("User with id=" + id + " was not found");
        }
        userRepository.deleteById(id);
    }


}
