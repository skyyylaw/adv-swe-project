package com.ontracked.controller;

import com.ontracked.model.CheckIn;
import com.ontracked.dto.checkin.CheckInRequest;
import com.ontracked.dto.checkin.CheckInResponse;
import com.ontracked.service.CheckInService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * This class defines the CheckInController which handles HTTP endpoint requests related
 * to CheckIn resources. It interacts with the MockCheckInService for data access.
 */
@RestController
@RequestMapping("/checkins")
public class CheckInController {

  private final CheckInService mockCheckInService;

  public CheckInController(CheckInService mockCheckInService) {
    this.mockCheckInService = mockCheckInService;
  }

  /**
   * Returns a list of all stored check-ins.
   *
   * @return A {@code ResponseEntity} containing a list of {@code CheckInResponse} objects
   *         with an HTTP 200 response if successful, or a message with an HTTP 500 if failed.
   */
  @GetMapping
  public ResponseEntity<?> getAllCheckIns() {
    System.out.println("Fetching all check-ins.");
    try {
      ArrayList<CheckInResponse> responses = new ArrayList<>();
      for (CheckIn c : mockCheckInService.getCheckIns()) {
        responses.add(CheckInResponse.toResponse(c));
      }
      return new ResponseEntity<>(responses, HttpStatus.OK);
    } catch (Exception e) {
      System.err.println(e);
      return new ResponseEntity<>("Error retrieving check-ins.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns the details of a specific CheckIn by its ID.
   *
   * @param id A {@code Long} representing the unique identifier of the CheckIn to retrieve.
   * @return A {@code ResponseEntity} containing either the {@code CheckInResponse} with
   *         an HTTP 200 if found, or an error message with HTTP 404 if not found.
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getCheckInById(@PathVariable Long id) {
    System.out.println("Fetching check-in with ID: " + id);
    for (CheckIn c : mockCheckInService.getCheckIns()) {
      if (c.getId().equals(id)) {
        return new ResponseEntity<>(CheckInResponse.toResponse(c), HttpStatus.OK);
      }
    }
    return new ResponseEntity<>("CheckIn not found.", HttpStatus.NOT_FOUND);
  }

  /**
   * Creates and stores a new {@code CheckIn}.
   *
   * @param request A {@code CheckInRequest} object containing the goalId, checkInDate, and notes.
   * @return A {@code ResponseEntity} containing the created {@code CheckInResponse} with
   *         an HTTP 201 if successful, or an error message with HTTP 500 if failed.
   */
  @PostMapping
  public ResponseEntity<?> createCheckIn(@RequestBody CheckInRequest request) {
    System.out.println("Create request received: " + request);
    try {
      CheckIn newCheckIn = CheckInRequest.toEntity(request);
      mockCheckInService.addCheckIn(newCheckIn);
      return new ResponseEntity<>(CheckInResponse.toResponse(newCheckIn), HttpStatus.CREATED);
    } catch (Exception e) {
      System.err.println("Error creating check-in: " + e.getMessage());
      return new ResponseEntity<>("Error creating check-in.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Updates an existing {@code CheckIn} by its ID.
   *
   * @param id A {@code Long} representing the unique identifier of the CheckIn to update.
   * @param request A {@code CheckInRequest} containing the new values.
   * @return A {@code ResponseEntity} containing the updated {@code CheckInResponse} with
   *         an HTTP 200 if successful, or HTTP 404 if not found.
   */
  @PatchMapping("/{id}")
  public ResponseEntity<?> updateCheckIn(@PathVariable Long id, @RequestBody CheckInRequest request) {
    System.out.println("Update request received for ID " + id);
    try {
      for (CheckIn existing : mockCheckInService.getCheckIns()) {
        if (existing.getId().equals(id)) {
          existing.setGoalId(request.getGoalId());
          existing.setCheckInDate(request.getCheckInDate());
          existing.setNotes(request.getNotes());
          mockCheckInService.updateCheckIn(existing);
          return new ResponseEntity<>(CheckInResponse.toResponse(existing), HttpStatus.OK);
        }
      }
      return new ResponseEntity<>("CheckIn not found.", HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      System.err.println("Error updating check-in: " + e.getMessage());
      return new ResponseEntity<>("Error updating check-in.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * A simple welcome endpoint for testing connectivity.
   *
   * @return A message indicating that the CheckIn API is running.
   */
  @GetMapping("/index")
  public String index() {
    System.out.println("CheckInController index endpoint called.");
    return "Welcome to the CheckIn API! Use /checkins to view all or POST to create new check-ins.";
  }
}
