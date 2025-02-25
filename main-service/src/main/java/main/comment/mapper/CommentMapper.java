package main.comment.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import main.comment.dto.CommentRequestDto;
import main.comment.dto.CommentResponseDto;
import main.comment.model.Comment;
import main.events.model.Event;
import main.users.model.User;


import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static CommentResponseDto toCommentDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .created(comment.getCreated())
                .lastUpdatedOn(comment.getLastUpdatedOn())
                .build();
    }

    public static Comment toComment(CommentRequestDto commentDto, Event event, User user) {
        return Comment.builder()
                .text(commentDto.getText())
                .event(event)
                .author(user)
                .created(LocalDateTime.now())
                .lastUpdatedOn(null)
                .build();
    }

    public  static Comment toComment(CommentResponseDto commentDto) {
        return Comment.builder()
                .text(commentDto.getText())
                .build();
    }
}