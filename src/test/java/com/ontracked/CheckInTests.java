package com.ontracked;


import com.ontracked.model.CheckIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.ontracked.service.CheckInService;
import com.ontracked.controller.CheckInController;
import com.ontracked.dto.checkin.CheckInRequest;
import com.ontracked.dto.checkin.CheckInResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CheckInTests {

  private CheckInService service;
  private CheckInController controller;

  @BeforeEach
  public void setup() {
    service = new CheckInService();
    controller = new CheckInController(service);
  }


  // Unit tests
 

  /**
   * Unit test for addCheckIn with typical valid input
   */
  @Test
  @DisplayName("Unit: addCheckIn with typical valid input")
  public void testAddCheckInTypical() {
    CheckIn checkIn = new CheckIn(1L, 101L, LocalDateTime.now(), "Routine update", LocalDateTime.now(), LocalDateTime.now(), 1);
    service.addCheckIn(checkIn);
    assertTrue(service.getCheckIns().contains(checkIn));
  }

  /**
   * Unit test for addCheckIn with atypical valid input (missing date)
   */
  @Test
  @DisplayName("Unit: addCheckIn with atypical valid input (missing date)")
  public void testAddCheckInAtypical() {
    CheckIn checkIn = new CheckIn(2L, 102L, null, "Forgot date", LocalDateTime.now(), LocalDateTime.now(), 1);
    service.addCheckIn(checkIn);
    assertNotNull(checkIn.getCheckInDate(), "Check-in date should be auto-filled");
  }

  /**
   * Unit test for addCheckIn with invalid input (null object)
   */
  @Test
  @DisplayName("Unit: addCheckIn with invalid input (null object)")
  public void testAddCheckInInvalid() {
    assertThrows(NullPointerException.class, () -> {
      service.addCheckIn(null);
    });
  }

  /**
   * Unit test for updateCheckIn with typical valid input
   */
  @Test
  @DisplayName("Unit: updateCheckIn with existing ID")
  public void testUpdateCheckInTypical() {
    CheckIn checkIn = new CheckIn(3L, 103L, LocalDateTime.now(), "Initial", LocalDateTime.now(), LocalDateTime.now(), 1);
    service.addCheckIn(checkIn);
    checkIn.setNotes("Updated notes");
    service.updateCheckIn(checkIn);
    assertEquals(
  "Updated notes",
  service.getCheckIns().stream()
    .filter(c -> c.getId().equals(3L))
    .findFirst()
    .orElseThrow()
    .getNotes()
);
  }

  /**
   * Unit test for updateCheckIn with atypical valid input (empty notes)
   */
  @Test
  @DisplayName("Unit: updateCheckIn with atypical valid input (empty notes)")
  public void testUpdateCheckInAtypical() {
    CheckIn checkIn = new CheckIn(4L, 104L, LocalDateTime.now(), "To clear", LocalDateTime.now(), LocalDateTime.now(), 1);
    service.addCheckIn(checkIn);
    checkIn.setNotes("");
    service.updateCheckIn(checkIn);
    assertEquals("", service.getCheckIns().get(0).getNotes());
  }

  /**
   * Unit test for updateCheckIn with invalid input (nonexistent ID)
   */
  @Test
  @DisplayName("Unit: updateCheckIn with invalid ID")
  public void testUpdateCheckInInvalid() {
    CheckIn nonExisting = new CheckIn(999L, 999L, LocalDateTime.now(), "Ghost", LocalDateTime.now(), LocalDateTime.now(), 1);
    int before = service.getCheckIns().size();
    service.updateCheckIn(nonExisting);
    assertEquals(before, service.getCheckIns().size());
  }


  // API tests

  /**
   * API test for createCheckIn with typical valid input
   */
  @Test
  @DisplayName("API: createCheckIn typical valid input")
  public void testCreateCheckInTypical() {
    CheckInRequest request = new CheckInRequest(201L, LocalDateTime.now(), "Feeling great!");
    ResponseEntity<?> response = controller.createCheckIn(request);
    assertEquals(201, response.getStatusCode().value());
    assertTrue(response.getBody() instanceof CheckInResponse);
  }

  /**
   * API test for createCheckIn with atypical valid input (empty notes)
   */
  @Test
  @DisplayName("API: createCheckIn atypical valid input (empty notes)")
  public void testCreateCheckInAtypical() {
    CheckInRequest request = new CheckInRequest(202L, LocalDateTime.now(), "");
    ResponseEntity<?> response = controller.createCheckIn(request);
    assertEquals(201, response.getStatusCode().value());
  }

  /**
   * API test for createCheckIn with invalid input (null request)
   */
  @Test
  @DisplayName("API: createCheckIn invalid input (null request)")
  public void testCreateCheckInInvalid() {
    ResponseEntity<?> response = controller.createCheckIn(null);
    assertEquals(500, response.getStatusCode().value());
  }

  /**
   * API test for getCheckInById with typical valid input
   */
  @Test
  @DisplayName("API: getCheckInById typical valid input")
  public void testGetCheckInByIdTypical() {
    CheckInRequest request = new CheckInRequest(301L, LocalDateTime.now(), "Daily update");
    controller.createCheckIn(request);
    CheckIn first = service.getCheckIns().get(0);
    ResponseEntity<?> response = controller.getCheckInById(first.getId());
    assertEquals(200, response.getStatusCode().value());
  }


  /**
   * API test for getCheckInById with atypical valid input (nonexistent but numeric ID)
   */
  @Test
  @DisplayName("API: getCheckInById atypical valid input (nonexistent but numeric ID)")
  public void testGetCheckInByIdAtypical() {
    ResponseEntity<?> response = controller.getCheckInById(9999L);
    assertEquals(404, response.getStatusCode().value());
  }

  /**
   * API test for getCheckInById with invalid input (null ID)
   */
  @Test
  @DisplayName("API: getCheckInById invalid input (null ID)")
  public void testGetCheckInByIdInvalid() {
    ResponseEntity<?> response = controller.getCheckInById(999L);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals("CheckIn not found.", response.getBody());
  }

  /**
   * API test for updateCheckIn with typical valid input
   */

  @Test
  @DisplayName("API: updateCheckIn typical valid input")
  public void testUpdateCheckInAPITypical() {
    CheckInRequest request = new CheckInRequest(401L, LocalDateTime.now(), "Initial note");
    controller.createCheckIn(request);
    CheckIn existing = service.getCheckIns().get(0);
    CheckInRequest updateRequest = new CheckInRequest(existing.getGoalId(), existing.getCheckInDate(), "Updated note");
    ResponseEntity<?> response = controller.updateCheckIn(existing.getId(), updateRequest);
    assertEquals(200, response.getStatusCode().value());
  }
  /**
   * API test for updateCheckIn with atypical valid input (empty notes)
   */
  @Test
  @DisplayName("API: updateCheckIn atypical valid input (empty notes)")
  public void testUpdateCheckInAPIAtypical() {
    CheckInRequest request = new CheckInRequest(402L, LocalDateTime.now(), "");
    controller.createCheckIn(request);
    CheckIn existing = service.getCheckIns().get(0);
    CheckInRequest updateRequest = new CheckInRequest(existing.getGoalId(), existing.getCheckInDate(), "");
    ResponseEntity<?> response = controller.updateCheckIn(existing.getId(), updateRequest);
    assertEquals(200, response.getStatusCode().value());
  }

  /**
   * API test for updateCheckIn with invalid input (nonexistent ID)
   */
  @Test
  @DisplayName("API: updateCheckIn invalid input (nonexistent ID)")
  public void testUpdateCheckInAPIInvalid() {
    CheckInRequest updateRequest = new CheckInRequest(999L, LocalDateTime.now(), "No such entry");
    ResponseEntity<?> response = controller.updateCheckIn(9999L, updateRequest);
    assertEquals(404, response.getStatusCode().value());
  }
}
