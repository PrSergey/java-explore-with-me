package ru.practicum.main.event.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.constant.CommentStatus;
import ru.practicum.main.event.dto.comment.CommentDto;
import ru.practicum.main.event.dto.comment.CommentMapper;
import ru.practicum.main.event.model.Comment;
import ru.practicum.main.event.services.CommentAdminService;
import ru.practicum.main.event.storage.CommentRepository;
import ru.practicum.main.excepsion.ExistenceException;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentAdminServiceImpl implements CommentAdminService {


    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Override
    public CommentDto blockerCommentById(long comId) {
        Comment comment = commentRepository.findById(comId)
                .orElseThrow(() -> new ExistenceException("Comment with id=" + comId + " was not found"));
        comment.setStatus(CommentStatus.BLOCKED);
        return commentMapper.toCommentDto(comment);
    }
}
