package main.requests.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import main.events.model.Event;
import main.users.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requestor;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    RequestStatus status;
}
