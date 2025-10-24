//package com.ontracked.controller;
//
//import com.ontracked.model.Goal;
//import com.ontracked.service.GoalService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.servlet.http.HttpServletRequest;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * REST controller exposing endpoints for managing {@link Goal} objects.
// *
// * <p>Base path: <b>/goal</b>
// * <p>Supported operations:
// * <ul>
// *   <li>GET /goal/index – simple health check endpoint</li>
// *   <li>GET /goal/retrieveOneGoal?id=... – fetch a goal by ID</li>
// *   <li>GET /goal/getAllGoals – fetch all stored goals</li>
// *   <li>POST /goal/saveOneGoal – persist a single goal</li>
// *   <li>POST /goal/saveMultipleGoals – persist multiple goals</li>
// * </ul>
// */
//@RestController
//@RequestMapping("/goal")
//public class GoalController {
//
//  private static final Logger logger = LoggerFactory.getLogger(GoalController.class);
//  private final GoalService goalService;
//
//  public GoalController(GoalService goalService) {
//    this.goalService = goalService;
//  }
//
//  // ------------------------------------------------------------------------
//  // Utility
//  // ------------------------------------------------------------------------
//
//  /**
//   * Logs request metadata for visibility and auditing.
//   *
//   * @param request  incoming HTTP request
//   * @param endpoint path or query string being accessed
//   */
//  private void logRequest(HttpServletRequest request, String endpoint) {
//    logger.info(
//            "Timestamp: {}, Origin: {}, Method: {}, Endpoint: {}",
//            java.time.Instant.now(),
//            request.getRemoteAddr(),
//            request.getMethod(),
//            endpoint
//    );
//  }
//
//  // ------------------------------------------------------------------------
//  // Endpoints
//  // ------------------------------------------------------------------------
//
//  /**
//   * Simple index endpoint for quick connectivity checks.
//   * Useful for testing controller reachability.
//   */
//  @GetMapping({"/", "/index"})
//  public String index(HttpServletRequest request) {
//    logRequest(request, "/index");
//    return "Goal Controller";
//  }
//
//  /**
//   * Retrieves a single {@link Goal} by ID.
//   *
//   * @param id      the goal's UUID
//   * @param request the HTTP request
//   * @return 200 + goal if found, 404 if not
//   */
//  @GetMapping("/retrieveOneGoal")
//  public ResponseEntity<?> retrieveOneGoal(@RequestParam String id, HttpServletRequest request) {
//    logRequest(request, "/retrieveOneGoal?id=" + id);
//
//    if (id == null || id.isBlank()) {
//      return ResponseEntity.badRequest().body("Missing or blank ID");
//    }
//
//    Goal goal = goalService.retrieveGoal(id);
//    if (goal == null) {
//      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Goal not found");
//    }
//    return ResponseEntity.ok(goal);
//  }
//
//  /**
//   * Returns all goals currently stored.
//   *
//   * @return 200 with list of goals, or 500 if load fails
//   */
//  @GetMapping("/getAllGoals")
//  public ResponseEntity<?> getAllGoals(HttpServletRequest request) {
//    logRequest(request, "/getAllGoals");
//
//    try {
//      List<Goal> goals = goalService.loadGoals();
//      return ResponseEntity.ok(goals);
//    } catch (Exception e) {
//      logger.error("Failed to load goals", e);
//      return ResponseEntity.internalServerError()
//              .body("Failed to load goals: " + e.getMessage());
//    }
//  }
//
//  /**
//   * Saves a list of {@link Goal} objects to the datastore.
//   *
//   * @param goals   list of goals to persist
//   * @param request HTTP request (for logging)
//   * @return 200 with the saved goals
//   */
//  @PostMapping("/saveMultipleGoals")
//  public ResponseEntity<?> saveMultipleGoals(@RequestBody List<Goal> goals, HttpServletRequest request) {
//    logRequest(request, "/saveMultipleGoals");
//
//    if (goals == null || goals.isEmpty()) {
//      return ResponseEntity.badRequest().body("Goal list cannot be empty");
//    }
//
//    try {
//      goalService.saveGoals(goals);
//      return ResponseEntity.ok(goals);
//    } catch (Exception e) {
//      logger.error("Failed to save multiple goals", e);
//      return ResponseEntity.internalServerError()
//              .body("Failed to save multiple goals: " + e.getMessage());
//    }
//  }
//
//  /**
//   * Saves a single {@link Goal}.
//   *
//   * @param goal    the goal to persist
//   * @param request HTTP request (for logging)
//   * @return 200 with the saved goal in a singleton list
//   */
//  @PostMapping("/saveOneGoal")
//  public ResponseEntity<?> saveOneGoal(@RequestBody Goal goal, HttpServletRequest request) {
//    logRequest(request, "/saveOneGoal");
//
//    if (goal == null) {
//      return ResponseEntity.badRequest().body("Goal cannot be null");
//    }
//
//    try {
//      List<Goal> goals = new ArrayList<>();
//      goals.add(goal);
//      goalService.saveGoals(goals);
//      return ResponseEntity.ok(goals);
//    } catch (Exception e) {
//      logger.error("Failed to save goal", e);
//      return ResponseEntity.internalServerError()
//              .body("Failed to save goal: " + e.getMessage());
//    }
//  }
//}
