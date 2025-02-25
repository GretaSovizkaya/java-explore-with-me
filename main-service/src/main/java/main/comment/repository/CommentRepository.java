package main.comment.repository;

import main.comment.dto.CountCommentsByEventDto;
import main.comment.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByEvent_Id(Long eventId, Pageable pageable);

    List<Comment> findByAuthor_Id(Long userId);

    Optional<Comment> findByAuthor_IdAndId(Long userId, Long id);

   @Query("SELECT new main.comment.dto.CountCommentsByEventDto(c.event.id, COUNT(c)) " +
            "FROM comments c WHERE c.event.id IN ?1 " +
            "GROUP BY c.event.id")
    List<CountCommentsByEventDto> countCommentByEvent(List<Long> eventIds);

    @Query("select c " +
            "from comments as c " +
            "where lower(c.text) like lower(concat('%', ?1, '%') )")
    List<Comment> search(String text, Pageable pageable);
}