package com.ontracked.controller;

import com.ontracked.model.Goal;
import com.ontracked.service.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
//@RequestMapping("/goals")
public class GoalController {

  private final GoalService goalService;

  public GoalController(GoalService goalService) {
    this.goalService = goalService;
  }

  @GetMapping({"/", "/index"})
  public String index() {
    return "Goal Controller";
  }

  @GetMapping({"/retrieveOneGoal"})
  public ResponseEntity<?> retieveOneGoal(@RequestParam String id) {
    Goal goal = this.goalService.retrieveGoal(id);
    if (goal == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<>(goal, HttpStatus.OK);
  }

  @GetMapping({"/getAllGoals"})
  public ResponseEntity<?> getAllGoals() {
    List<Goal> goals = this.goalService.loadGoals();
    return new ResponseEntity<>(goals, HttpStatus.OK);
  }

  @PostMapping({"/saveMultipleGoals"})
  public ResponseEntity<?>  saveMultipleGoals(@RequestBody List<Goal> goals) {
    this.goalService.saveGoals(goals);
    return new ResponseEntity<>(goals, HttpStatus.OK);
  }

  @PostMapping({"/saveOneGoal"})
  public ResponseEntity<?>  saveOneGoal(@RequestBody Goal goal) {
    List<Goal> goals = new ArrayList<>();
    goals.add(goal);
    this.goalService.saveGoals(goals);
    return new ResponseEntity<>(goals, HttpStatus.OK);
  }

  /*
  TODO:
  - Delete Goal
  - Update Goal
  - Retrieve Goals
  - retrieve goals associated with one user
   */

}
