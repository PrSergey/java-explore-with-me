package ru.practicum.main.event.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.constant.CommentStatus;
import ru.practicum.main.event.dto.comment.CommentDto;
import ru.practicum.main.event.dto.comment.CommentMapper;
import ru.practicum.main.event.dto.comment.NewCommentDto;
import ru.practicum.main.event.dto.comment.UpdateCommentUserRequestDto;
import ru.practicum.main.event.model.Comment;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.services.CommentPrivateService;
import ru.practicum.main.event.storage.CommentRepository;
import ru.practicum.main.event.storage.EventRepository;
import ru.practicum.main.excepsion.ExistenceException;
import ru.practicum.main.excepsion.ValidationException;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentPrivateServiceImpl implements CommentPrivateService {


    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto saveCommentForEvent(long userId, long eventId, NewCommentDto commentDto) {
        if (commentRepository.existsAllByCreator_IdAndEvent_id(userId, eventId)) {
            throw new ValidationException("User with id=" + userId + " commented on the event with id=" + eventId);
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException("Event with id=" + eventId + " was not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException("User with id=" + eventId + " was not found"));
        Comment comment = commentMapper.fromNewCommentDto(commentDto);
        comment.setEvent(event);
        comment.setCreator(user);
        comment.setCreated(LocalDateTime.now());
        comment.setStatus(CommentStatus.PUBLISHED);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateComment(long userId, long comId, UpdateCommentUserRequestDto updateComment) {
        Comment comment = getCommentAndCheckUserAndEvent(userId, comId);
        commentMapper.updateUserOfComment(updateComment, comment);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteComment(long userId, long comId) {
        Comment comment = getCommentAndCheckUserAndEvent(userId, comId);
        comment.setStatus(CommentStatus.DELETE);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(long userId, long comId) {
        Comment comment = getCommentAndCheckUserAndEvent(userId, comId);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentByUser(long userId, int from, int size) {
        PageRequest sortedByEvent = PageRequest.of(from / size, size, Sort.by("event_id"));
        List<Comment> comments = commentRepository.findAllByCreator_IdAndStatus(userId, CommentStatus.PUBLISHED,
                sortedByEvent);
        return comments.stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
    }

    private Comment getCommentAndCheckUserAndEvent(long userId, long comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new ExistenceException("Comment with id=" + comId + " was not found"));
        if (comment.getCreator().getId() != userId)
            throw new ValidationException("User with id=" + userId + "is not the creator of the comment with id="
                    + comId);
        CommentStatus status = comment.getStatus();
        if (!status.equals(CommentStatus.PUBLISHED))
            throw new ExistenceException("Comment with id=" + comId + " was not found");
        return comment;
    }


}
