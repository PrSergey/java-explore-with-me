package ru.practicum.main.event.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.main.event.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    List<Event> findAllByInitiator_Id(long userId, PageRequest pageRequest);

    List<Event> findAllByIdIn(List<Long> ids);

}
