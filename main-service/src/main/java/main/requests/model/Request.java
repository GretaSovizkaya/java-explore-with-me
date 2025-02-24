package main.requests.model;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "create_date", nullable = false)
    LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    User requester;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    RequestStatus status;
}
