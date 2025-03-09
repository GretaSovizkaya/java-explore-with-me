package main.comment.controller.publical;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.comment.dto.CommentResponseDto;
import main.comment.service.CommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class CommentPublicController {
    CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentResponseDto> getAllCommentsByEvent(@PathVariable Long eventId,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = "10") @Positive Integer size) {

        log.info("Получение всех комментариев по событию с id = {} ", eventId);

        return commentService.getCommentByEvent(eventId, from, size);
    }
}