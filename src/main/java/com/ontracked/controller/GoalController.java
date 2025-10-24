package com.ontracked.controller;

import com.ontracked.model.Goal;
import com.ontracked.service.GoalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller that exposes endpoints for managing {@link Goal} objects.
 *
 * <p>Provides operations for retrieving, saving, and listing goals.
 * Each endpoint delegates the core logic to the {@link GoalService} layer.
 *
 * <p>Base path: <b>/goal</b>
 */
@RestController
@RequestMapping("/goal")
public class GoalController {

  private static final Logger logger = LoggerFactory.getLogger(GoalController.class);
  private final GoalService goalService;

  public GoalController(GoalService goalService) {
    this.goalService = goalService;
  }

  private void logRequest(HttpServletRequest request, String endpoint) {
    logger.info(
            "Timestamp: {}, Origin: {}, Method: {}, Endpoint: {}",
            java.time.Instant.now(),
            request.getRemoteAddr(),
            request.getMethod(),
            endpoint
    );
  }

  @GetMapping({"/", "/index"})
  public String index(HttpServletRequest request) {
    logRequest(request, "/index");
    return "Goal Controller";
  }

  @GetMapping("/retrieveOneGoal")
  public ResponseEntity<?> retieveOneGoal(@RequestParam String id, HttpServletRequest request) {
    logRequest(request, "/retrieveOneGoal?id=" + id);
    Goal goal = this.goalService.retrieveGoal(id);
    if (goal == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(goal, HttpStatus.OK);
  }

  @GetMapping("/getAllGoals")
  public ResponseEntity<?> getAllGoals(HttpServletRequest request) {
    logRequest(request, "/getAllGoals");
    try {
      List<Goal> goals = this.goalService.loadGoals();
      return new ResponseEntity<>(goals, HttpStatus.OK);
    } catch (Exception e) {
      logger.error("Failed to load goals", e);
      return new ResponseEntity<>("Failed to load goals: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/saveMultipleGoals")
  public ResponseEntity<?> saveMultipleGoals(@RequestBody List<Goal> goals, HttpServletRequest request) {
    logRequest(request, "/saveMultipleGoals");
    this.goalService.saveGoals(goals);
    return new ResponseEntity<>(goals, HttpStatus.OK);
  }

  @PostMapping("/saveOneGoal")
  public ResponseEntity<?> saveOneGoal(@RequestBody Goal goal, HttpServletRequest request) {
    logRequest(request, "/saveOneGoal");
    List<Goal> goals = new ArrayList<>();
    goals.add(goal);
    this.goalService.saveGoals(goals);
    return new ResponseEntity<>(goals, HttpStatus.OK);
  }
}
