package ru.practicum.main.user.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findAllByIdIn(List<Long> userId, PageRequest pageRequest);

    boolean existsAllByName(String name);

}
