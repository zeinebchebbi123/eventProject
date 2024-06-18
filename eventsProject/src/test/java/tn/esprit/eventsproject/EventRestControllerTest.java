package tn.esprit.eventsproject;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.eventsproject.controllers.EventRestController;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.services.IEventServices;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@WebMvcTest(EventRestController.class)
public class EventRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEventServices eventServices;

    @Autowired
    private ObjectMapper objectMapper;

    private Participant sampleParticipant;
    private Event sampleEvent;
    private Logistics sampleLogistics;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleParticipant = new Participant(1, "John", "Doe", Tache.INVITE, new HashSet<>());
        sampleEvent = new Event(1, "Conference", LocalDate.now(), LocalDate.now().plusDays(1), 500, new HashSet<>(), new HashSet<>());
        sampleLogistics = new Logistics(1, "Sound System", true, 100, 2);

        // Ensure relationships are correctly established
        sampleEvent.getParticipants().add(sampleParticipant);
        sampleParticipant.getEvents().add(sampleEvent);
        sampleEvent.getLogistics().add(sampleLogistics);
    }

    @Test
    public void testAddParticipant() throws Exception {
        when(eventServices.addParticipant(sampleParticipant)).thenReturn(sampleParticipant);

        String participantJson = objectMapper.writeValueAsString(sampleParticipant);

        mockMvc.perform(post("/event/addPart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(participantJson))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("nom").value("John"))
//                .andExpect(jsonPath("prenom").value("Doe"));
    }

    @Test
    public void testAddEventPart() throws Exception {
        int idPart = 1;

        when(eventServices.addAffectEvenParticipant(sampleEvent, idPart)).thenReturn(sampleEvent);

        mockMvc.perform(post("/event/addEvent/{id}", idPart)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Conference"));
    }

    @Test
    public void testAddEvent() throws Exception {
        when(eventServices.addAffectEvenParticipant(sampleEvent)).thenReturn(sampleEvent);

        mockMvc.perform(post("/event/addEvent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Conference"));
    }

    @Test
    public void testAddAffectLog() throws Exception {
        String descriptionEvent = "Conference";

        when(eventServices.addAffectLog(sampleLogistics, descriptionEvent)).thenReturn(sampleLogistics);

        mockMvc.perform(put("/event/addAffectLog/{description}", descriptionEvent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleLogistics)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Sound System"));
    }

    @Test
    public void testGetLogistiquesDates() throws Exception {
        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = LocalDate.now().plusDays(1);
        List<Logistics> logisticsList = Arrays.asList(
                new Logistics(1, "Sound System", true, 100, 2),
                new Logistics(2, "Projector", false, 50, 1)
        );

        when(eventServices.getLogisticsDates(dateDebut, dateFin)).thenReturn(logisticsList);

        mockMvc.perform(get("/event/getLogs/{d1}/{d2}", dateDebut, dateFin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Sound System"))
                .andExpect(jsonPath("$[1].description").value("Projector"));
    }
}
