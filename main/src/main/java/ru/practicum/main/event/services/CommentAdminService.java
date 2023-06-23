package ru.practicum.main.event.services;

import ru.practicum.main.event.dto.comment.CommentDto;

public interface CommentAdminService {
    CommentDto blockerCommentById(long comId);
}
