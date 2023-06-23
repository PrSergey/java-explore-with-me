package ru.practicum.main.event.services;

import ru.practicum.main.event.dto.comment.CommentDto;
import ru.practicum.main.event.dto.comment.NewCommentDto;
import ru.practicum.main.event.dto.comment.UpdateCommentUserRequestDto;

import java.util.List;

public interface CommentPrivateService {

    CommentDto saveCommentForEvent(long userId, long eventId, NewCommentDto commentDto);

    CommentDto updateComment(long userId, long comId, UpdateCommentUserRequestDto updateComment);

    void deleteComment(long userId, long comId);

    CommentDto getCommentById(long userId, long comId);

    List<CommentDto> getCommentByUser(long userId, int from, int size);
}
