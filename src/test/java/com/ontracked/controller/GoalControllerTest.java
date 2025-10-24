//package com.ontracked.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.ontracked.model.Goal;
//import com.ontracked.model.GoalStatus;
//import com.ontracked.service.GoalService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentMatchers;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//import java.util.UUID;
//
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(controllers = GoalController.class)
//class GoalControllerTest {
//
//  @Autowired private MockMvc mockMvc;
//  @Autowired private ObjectMapper objectMapper;
//
//  @MockBean private GoalService goalService;
//
//  private Goal sampleGoal() {
//    Goal g = new Goal("owner-1");
//    g.setId(UUID.randomUUID().toString());
//    g.setTitle("Title");
//    g.setDescription("Desc");
//    g.setStatus(GoalStatus.ACTIVE);
//    g.setLatestPercentage(30);
//    return g;
//  }
//
//  @Test
//  @DisplayName("GET /goal/index returns plain text")
//  void index() throws Exception {
//    mockMvc.perform(get("/goal/index"))
//            .andExpect(status().isOk())
//            .andExpect(content().string("Goal Controller"));
//  }
//
//  @Test
//  @DisplayName("GET /goal/getAllGoals returns list")
//  void getAllGoals_ok() throws Exception {
//    List<Goal> goals = List.of(sampleGoal(), sampleGoal());
//    when(goalService.loadGoals()).thenReturn(goals);
//
//    mockMvc.perform(get("/goal/getAllGoals"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.length()").value(2));
//  }
//
//  @Test
//  @DisplayName("GET /goal/getAllGoals handles service error")
//  void getAllGoals_error() throws Exception {
//    when(goalService.loadGoals()).thenThrow(new RuntimeException("boom"));
//
//    mockMvc.perform(get("/goal/getAllGoals"))
//            .andExpect(status().isInternalServerError())
//            .andExpect(content().string(org.hamcrest.Matchers.containsString("Failed to load goals")));
//  }
//
//  @Test
//  @DisplayName("GET /goal/retrieveOneGoal returns 200 when found")
//  void retrieveOneGoal_found() throws Exception {
//    Goal g = sampleGoal();
//    when(goalService.retrieveGoal(g.getId())).thenReturn(g);
//
//    mockMvc.perform(get("/goal/retrieveOneGoal").param("id", g.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.id").value(g.getId()))
//            .andExpect(jsonPath("$.ownerId").value("owner-1"));
//  }
//
//  @Test
//  @DisplayName("GET /goal/retrieveOneGoal returns 404 when missing")
//  void retrieveOneGoal_missing() throws Exception {
//    when(goalService.retrieveGoal("nope")).thenReturn(null);
//
//    mockMvc.perform(get("/goal/retrieveOneGoal").param("id", "nope"))
//            .andExpect(status().isNotFound());
//  }
//
//  @Test
//  @DisplayName("POST /goal/saveOneGoal accepts a Goal and echoes list")
//  void saveOneGoal_ok() throws Exception {
//    Goal g = sampleGoal();
//    doNothing().when(goalService).saveGoals(ArgumentMatchers.anyList());
//
//    mockMvc.perform(
//                    post("/goal/saveOneGoal")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(g)))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.length()").value(1))
//            .andExpect(jsonPath("$[0].ownerId").value("owner-1"));
//  }
//
//  @Test
//  @DisplayName("POST /goal/saveMultipleGoals accepts list and echoes it")
//  void saveMultipleGoals_ok() throws Exception {
//    List<Goal> goals = List.of(sampleGoal(), sampleGoal());
//    doNothing().when(goalService).saveGoals(ArgumentMatchers.anyList());
//
//    mockMvc.perform(
//                    post("/goal/saveMultipleGoals")
//                            .contentType(MediaType.APPLICATION_JSON)
//                            .content(objectMapper.writeValueAsString(goals)))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//            .andExpect(jsonPath("$.length()").value(2));
//  }
//}
