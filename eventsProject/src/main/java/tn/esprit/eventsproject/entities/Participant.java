package tn.esprit.eventsproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Participant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idPart;
    String nom;
    String prenom;
    @Enumerated(EnumType.STRING)
    Tache tache;

    @ManyToMany(mappedBy = "participants")
    @Builder.Default
    @JsonIgnoreProperties("participants")  // Ignore serialization of the 'participants' field
    Set<Event> events = new HashSet<>();

    public void addEvent(Event event) {
        events.add(event);
        event.getParticipants().add(this);  // Ensure bidirectional relationship
    }

    public void removeEvent(Event event) {
        events.remove(event);
        event.getParticipants().remove(this);  // Ensure bidirectional relationship
    }

    public Participant(int i, String john, String doe, Tache tache,Event events) {
    }
}
