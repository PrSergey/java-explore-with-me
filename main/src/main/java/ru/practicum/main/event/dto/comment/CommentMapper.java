package ru.practicum.main.event.dto.comment;


import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.main.event.dto.events.EventMapper;
import ru.practicum.main.event.model.Comment;
import ru.practicum.main.user.dto.UserMapper;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public interface CommentMapper {

    Comment fromCommentDto(CommentDto commentDto);

    Comment fromNewCommentDto(NewCommentDto newCommentDto);

    CommentDto toCommentDto(Comment comment);

    void updateUserOfComment(UpdateCommentUserRequestDto updateComment, @MappingTarget Comment comment);

}
