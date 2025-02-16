package main.compilations.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import main.events.model.Event;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToMany
    @JoinTable(name = "compilations_to_event", joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    Set<Event> events;
    Boolean pinned;
    @Column(name = "title", nullable = false, length = 50)
    String title;

}
