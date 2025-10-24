package com.ontracked.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ontracked.model.Goal;
import com.ontracked.model.GoalStatus;
import com.ontracked.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GoalController.class)
class GoalControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GoalService goalService;

  private ObjectMapper mapper;
  private Goal goal1, goal2;

  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    goal1 = new Goal("Alice");
    goal1.setId(UUID.randomUUID().toString());
    goal1.setTitle("Finish MVP");
    goal1.setStatus(GoalStatus.ACTIVE);

    goal2 = new Goal("Bob");
    goal2.setId(UUID.randomUUID().toString());
    goal2.setTitle("Ship Beta");
    goal2.setStatus(GoalStatus.ACTIVE);
  }

  // --- GET /goal/getAllGoals ---
  @Test
  void testGetAllGoals_validTypical() throws Exception {
    when(goalService.loadGoals()).thenReturn(List.of(goal1, goal2));

    mockMvc.perform(get("/goal/getAllGoals").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].ownerId", is("Alice")));

    verify(goalService, times(1)).loadGoals();
  }

  @Test
  void testGetAllGoals_emptyListAtypical() throws Exception {
    when(goalService.loadGoals()).thenReturn(List.of());

    mockMvc.perform(get("/goal/getAllGoals"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

    verify(goalService, times(1)).loadGoals();
  }

  @Test
  void testGetAllGoals_internalErrorInvalid() throws Exception {
    when(goalService.loadGoals()).thenThrow(new RuntimeException("I/O failed"));

    mockMvc.perform(get("/goal/getAllGoals"))
            .andExpect(status().isInternalServerError());
  }

  // --- GET /goal/retrieveOneGoal?id= ---
  @Test
  void testRetrieveOneGoal_foundTypical() throws Exception {
    when(goalService.retrieveGoal(goal1.getId())).thenReturn(goal1);

    mockMvc.perform(get("/goal/retrieveOneGoal?id=" + goal1.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ownerId", is("Alice")));
  }

  @Test
  void testRetrieveOneGoal_notFoundInvalid() throws Exception {
    when(goalService.retrieveGoal("missing-id")).thenReturn(null);

    mockMvc.perform(get("/goal/retrieveOneGoal?id=missing-id"))
            .andExpect(status().isNotFound());
  }

  @Test
  void testRetrieveOneGoal_blankIdAtypical() throws Exception {
    mockMvc.perform(get("/goal/retrieveOneGoal?id="))
            .andExpect(status().isNotFound()); // current controller returns 404 for blank
  }

  // --- POST /goal/saveOneGoal ---
  @Test
  void testSaveOneGoal_validTypical() throws Exception {
    String payload = mapper.writeValueAsString(goal1);

    mockMvc.perform(post("/goal/saveOneGoal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].ownerId", is("Alice")));

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Goal>> captor = ArgumentCaptor.forClass(List.class);
    verify(goalService, times(1)).saveGoals(captor.capture());
    assert captor.getValue().get(0).getOwnerId().equals("Alice");
  }

  @Test
  void testSaveOneGoal_atypicalEmptyTitle() throws Exception {
    Goal g = new Goal("Alice");
    g.setTitle("");
    String payload = mapper.writeValueAsString(g);

    mockMvc.perform(post("/goal/saveOneGoal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].ownerId", is("Alice")));
  }

  @Test
  void testSaveOneGoal_invalidMissingOwner() throws Exception {
    String invalidJson = """
        { "id": "abc123", "title": "Missing owner" }
        """;

    mockMvc.perform(post("/goal/saveOneGoal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(invalidJson))
            .andExpect(status().isBadRequest());
  }

  // --- POST /goal/saveMultipleGoals ---
  @Test
  void testSaveMultipleGoals_twoClientsTypical() throws Exception {
    List<Goal> payloadList = List.of(goal1, goal2);
    String payload = mapper.writeValueAsString(payloadList);

    mockMvc.perform(post("/goal/saveMultipleGoals")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].ownerId", is("Alice")))
            .andExpect(jsonPath("$[1].ownerId", is("Bob")));

    verify(goalService, times(1)).saveGoals(anyList());
  }

  @Test
  void testSaveMultipleGoals_emptyListAtypical() throws Exception {
    String payload = mapper.writeValueAsString(List.of());

    mockMvc.perform(post("/goal/saveMultipleGoals")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void testSaveMultipleGoals_invalidMalformedJson() throws Exception {
    mockMvc.perform(post("/goal/saveMultipleGoals")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{bad json"))
            .andExpect(status().is4xxClientError());
  }

  // --- Integration-style test (write + read flow) ---
  @Test
  void testWriteThenReadFlow() throws Exception {
    when(goalService.loadGoals()).thenReturn(List.of(goal1));
    when(goalService.retrieveGoal(goal1.getId())).thenReturn(goal1);

    mockMvc.perform(post("/goal/saveOneGoal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(goal1)))
            .andExpect(status().isOk());

    mockMvc.perform(get("/goal/retrieveOneGoal?id=" + goal1.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ownerId", is("Alice")));
  }

  // --- Logging verification ---
  @Test
  void testSaveGoals_loggedOncePerCall() throws Exception {
    String payload = mapper.writeValueAsString(goal1);

    mockMvc.perform(post("/goal/saveOneGoal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
            .andExpect(status().isOk());

    verify(goalService, times(1)).saveGoals(anyList());
  }
}
