package main.comment.controller.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import main.comment.dto.CommentResponseDto;
import main.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentAdminController {
    CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        log.info("Удаление комментария id = {} ", commentId);
        commentService.deleteAdmin(commentId);
    }

    @GetMapping("/search")
    public List<CommentResponseDto> searchComment(@RequestParam(name = "text") String text,
                                                  @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Поиск комментариев c текстом = {}", text);
        return commentService.searchComment(text, from, size);
    }
}