package com.ontracked.controller;

import com.ontracked.model.Goal;
import com.ontracked.service.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  private final GoalService goalService;

  /**
   * Constructs a controller with a dependency-injected {@link GoalService}.
   *
   * @param goalService the service responsible for goal persistence and retrieval
   */
  public GoalController(GoalService goalService) {
    this.goalService = goalService;
  }

  /**
   * Simple health/index endpoint.
   *
   * @return a plain text confirmation that the controller is active
   */
  @GetMapping({"/", "/index"})
  public String index() {
    return "Goal Controller";
  }

  /**
   * Retrieves a single goal by its unique ID.
   *
   * @param id the ID of the goal to retrieve
   * @return {@code 200 OK} and the {@link Goal} if found;
   *         {@code 404 NOT FOUND} if no goal exists with the given ID
   */
  @GetMapping("/retrieveOneGoal")
  public ResponseEntity<?> retieveOneGoal(@RequestParam String id) {
    Goal goal = this.goalService.retrieveGoal(id);
    if (goal == null) {
      // Goal not found -> return 404
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    // Found -> return goal object in response body
    return new ResponseEntity<>(goal, HttpStatus.OK);
  }

  /**
   * Retrieves all stored goals.
   *
   * @return {@code 200 OK} with a JSON array of all goals if successful;
   *         {@code 500 INTERNAL SERVER ERROR} if an I/O or parsing error occurs
   */
  @GetMapping("/getAllGoals")
  public ResponseEntity<?> getAllGoals() {
    try {
      List<Goal> goals = this.goalService.loadGoals();
      return new ResponseEntity<>(goals, HttpStatus.OK);
    } catch (Exception e) {
      // Catch and wrap file I/O or parsing errors
      return new ResponseEntity<>("Failed to load goals: " + e.getMessage(),
              HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Saves multiple {@link Goal} objects at once.
   *
   * @param goals list of goal objects to save
   * @return {@code 200 OK} and echoes back the saved goals
   */
  @PostMapping("/saveMultipleGoals")
  public ResponseEntity<?> saveMultipleGoals(@RequestBody List<Goal> goals) {
    this.goalService.saveGoals(goals);
    return new ResponseEntity<>(goals, HttpStatus.OK);
  }

  /**
   * Saves a single {@link Goal}.
   *
   * @param goal the goal object to save
   * @return {@code 200 OK} and echoes back the saved goal in a list
   */
  @PostMapping("/saveOneGoal")
  public ResponseEntity<?> saveOneGoal(@RequestBody Goal goal) {
    List<Goal> goals = new ArrayList<>();
    goals.add(goal);
    this.goalService.saveGoals(goals);
    return new ResponseEntity<>(goals, HttpStatus.OK);
  }

  /*
   * TODO (future work):
   * - DELETE endpoint for removing a goal
   * - PUT/PATCH endpoint for updating an existing goal
   * - Retrieval endpoint to list goals by user (e.g., /goal/user/{ownerId})
   */
}
