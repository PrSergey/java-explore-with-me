package ru.practicum.main.event.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main.constant.CommentStatus;
import ru.practicum.main.event.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByCreator_IdAndStatus(long userId, CommentStatus status, PageRequest pageRequest);

    boolean existsAllByCreator_IdAndEvent_id(long userId, long eventId);

    List<Comment> findAllByEvent_IdInAndStatus(List<Long> eventIds, CommentStatus status);

    List<Comment> findAllByEvent_IdAndStatus(long eventId, CommentStatus status);

}
