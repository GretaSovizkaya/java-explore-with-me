package main.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import main.comment.dto.CommentRequestDto;
import main.comment.dto.CommentResponseDto;
import main.comment.mapper.CommentMapper;
import main.comment.model.Comment;
import main.comment.repository.CommentRepository;
import main.events.model.Event;
import main.events.repository.EventRepository;
import main.exceptions.NotFoundException;
import main.exceptions.ValidatetionConflict;
import main.users.model.User;
import main.users.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {
    CommentRepository commentRepository;
    UserRepository userRepository;
    EventRepository eventRepository;


    @Override
    @Transactional
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto updateCommentDto) {
        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        checkAuthorComment(user, comment);
        LocalDateTime updateTime = LocalDateTime.now();

        comment.setText(updateCommentDto.getText());
        comment.setLastUpdatedOn(updateTime);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentByUser(Long userId) {
        checkUser(userId);
        List<Comment> commentList = commentRepository.findByAuthor_Id(userId);
        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentResponseDto getCommentByUserAndCommentId(Long userId, Long commentId) {
        checkUser(userId);
        Comment comment = commentRepository.findByAuthor_IdAndId(userId, commentId).orElseThrow(() -> new NotFoundException(
                String.format("У пользователя c id = {}  не найден комментарий с id = {}", userId, commentId)));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentByEvent(Long eventId, Integer from, Integer size) {
        Event event = checkEvent(eventId);
        PageRequest pageable = PageRequest.of(from / size, size);
        List<Comment> commentList = commentRepository.findAllByEvent_Id(eventId, pageable);
        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        User user = checkUser(userId);
        Comment comment = checkComment(commentId);
        checkAuthorComment(user, comment);
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteAdmin(Long commentId) {
        Comment comment = checkComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public List<CommentResponseDto> searchComment(String text, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Comment> commentList = commentRepository.search(text, pageable);
        return commentList.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, Long eventId, CommentRequestDto commentDto) {
        Event event = checkEvent(eventId);
        User user = checkUser(userId);
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(commentDto, event, user)));
    }

    private Comment checkComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Комментарий c id = {}  не найден" + id));
    }

    private void checkAuthorComment(User user, Comment comment) {
        if (!comment.getAuthor().equals(user)) {
            throw new ValidatetionConflict("Пользователь не является автором комментария");
        }
    }

    private Event checkEvent(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Событие с id = {}  не найдено" + id));
    }

    private User checkUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь c  id = {}  не найден" + id));
    }

}