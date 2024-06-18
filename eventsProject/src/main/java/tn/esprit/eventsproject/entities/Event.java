package tn.esprit.eventsproject.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Event implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idEvent;
    String description;
    LocalDate dateDebut;
    LocalDate dateFin;




    @ManyToMany(mappedBy = "events")
    @Builder.Default  // Ensures that the set is initialized with a default value
    Set<Participant> participants = new HashSet<>();  // Initialize participants set

    @OneToMany(mappedBy = "event")
    @Builder.Default  // Ensures that the set is initialized with a default value
    Set<Logistics> logistics = new HashSet<>();  // Initialize logistics set

    public void addParticipant(Participant participant) {
        participants.add(participant);
        participant.getEvents().add(this);  // Ensure bidirectional relationship
    }

    public void removeParticipant(Participant participant) {
        participants.remove(participant);
        participant.getEvents().remove(this);  // Ensure bidirectional relationship
    }


    float cout;


}
