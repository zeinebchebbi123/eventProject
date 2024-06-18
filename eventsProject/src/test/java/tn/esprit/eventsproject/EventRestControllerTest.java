package tn.esprit.eventsproject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
import java.util.Set;

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

        sampleParticipant = Participant.builder()

                .nom("ZEINEB")
                .prenom("CHEBBBI")
                .tache(Tache.INVITE)
                .build();



        sampleLogistics = Logistics.builder()

                .description("Sound System")
                .reserve(true)
                .prixUnit(100)
                .quantite(2)
                .build();




    }

    @Test
    public void testAddParticipant() throws Exception {
        Participant participant = Participant.builder()
                .nom("TEST")
                .prenom("TEST200")
                .tache(Tache.ORGANISATEUR)
                .build();

        when(eventServices.addParticipant(participant)).thenReturn(participant);

        String participantJson = objectMapper.writeValueAsString(participant);

        MvcResult result = mockMvc.perform(post("/event/addPart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(participantJson))
                .andExpect(status().isOk())
                .andReturn();

        Participant savedParticipant = eventServices.addParticipant(participant);
        Assertions.assertTrue(savedParticipant.getNom().length() > 3);
        Assertions.assertTrue(savedParticipant.getPrenom().length() > 3);
        Assertions.assertEquals(Tache.ORGANISATEUR, savedParticipant.getTache());
    }

    @Test
    public void testAddEventPart() throws Exception {
        int idPart = 1;
        sampleEvent = Event.builder()

                .description("Conference")
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now().plusDays(1))

                .participants(new HashSet<>())
                .logistics(new HashSet<>())
                .build();
        // Mock the service method to return the sampleEvent
        when(eventServices.addAffectEvenParticipant(any(Event.class), eq(idPart))).thenReturn(sampleEvent);

        // Perform the POST request to add the event
        mockMvc.perform(post("/event/addEvent/{id}", idPart)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleEvent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Conference"));

        // Verify that the service method was called with the correct arguments
       // verify(eventServices, times(1)).addAffectEvenParticipant(eq(sampleEvent), eq(idPart));
    }




    @Test
    public void testAddAffectLog() throws Exception {
        String descriptionEvent = "Conference";
     Logistics logistics=   Logistics.builder()

                .description("Sound System")
                .reserve(true)
                .prixUnit(100)
                .quantite(2)
                .build();
                Logistics.builder();
        when(eventServices.addAffectLog(sampleLogistics, descriptionEvent)).thenReturn(logistics);

        mockMvc.perform(put("/event/addAffectLog/{description}", descriptionEvent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logistics)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetLogistiquesDates() throws Exception {
        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = LocalDate.now().plusDays(1);
        List<Logistics> logisticsList = Arrays.asList(
                Logistics.builder()

                        .description("Sound System")
                        .reserve(true)
                        .prixUnit(100)
                        .quantite(2)
                        .build(),
                Logistics.builder()

                        .description("Projector")
                        .reserve(false)
                        .prixUnit(50)
                        .quantite(1)
                        .build()
        );

        when(eventServices.getLogisticsDates(dateDebut, dateFin)).thenReturn(logisticsList);

        mockMvc.perform(get("/event/getLogs/{d1}/{d2}", dateDebut, dateFin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Sound System"))
                .andExpect(jsonPath("$[1].description").value("Projector"));
    }
}
