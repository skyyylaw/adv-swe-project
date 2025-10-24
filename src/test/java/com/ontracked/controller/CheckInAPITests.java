package com.ontracked.controller;

import com.ontracked.model.CheckIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.servlet.http.HttpServletRequest;
import com.ontracked.service.CheckInService;
import com.ontracked.controller.CheckInController;
import com.ontracked.dto.checkin.CheckInRequest;
import com.ontracked.dto.checkin.CheckInResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CheckInAPITests {

  private CheckInService service;
  private CheckInController controller;
  @Mock
  private HttpServletRequest mockRequest;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    service = new CheckInService();
    controller = new CheckInController(service);
  }

  // API tests

  /**
   * API test for createCheckIn with typical valid input
   */
  @Test
  @DisplayName("API: createCheckIn typical valid input")
  public void testCreateCheckInTypical() {
    CheckInRequest request = new CheckInRequest(201L, LocalDateTime.now(), "Feeling great!");
    ResponseEntity<?> response = controller.createCheckIn(request, mockRequest);
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
    ResponseEntity<?> response = controller.createCheckIn(request, mockRequest);
    assertEquals(201, response.getStatusCode().value());
  }

  /**
   * API test for createCheckIn with invalid input (null request)
   */
  @Test
  @DisplayName("API: createCheckIn invalid input (null request)")
  public void testCreateCheckInInvalid() {
    ResponseEntity<?> response = controller.createCheckIn(null, mockRequest);
    assertEquals(500, response.getStatusCode().value());
  }

  /**
   * API test for getCheckInById with typical valid input
   */
  @Test
  @DisplayName("API: getCheckInById typical valid input")
  public void testGetCheckInByIdTypical() {
    CheckInRequest request = new CheckInRequest(301L, LocalDateTime.now(), "Daily update");
    controller.createCheckIn(request, mockRequest);
    CheckIn first = service.getCheckIns().get(0);
    ResponseEntity<?> response = controller.getCheckInById(first.getId(), mockRequest);
    assertEquals(200, response.getStatusCode().value());
  }


  /**
   * API test for getCheckInById with atypical valid input (nonexistent but numeric ID)
   */
  @Test
  @DisplayName("API: getCheckInById atypical valid input (nonexistent but numeric ID)")
  public void testGetCheckInByIdAtypical() {
    ResponseEntity<?> response = controller.getCheckInById(9999L, mockRequest);
    assertEquals(404, response.getStatusCode().value());
  }

  /**
   * API test for getCheckInById with invalid input (null ID)
   */
  @Test
  @DisplayName("API: getCheckInById invalid input (null ID)")
  public void testGetCheckInByIdInvalid() {
    ResponseEntity<?> response = controller.getCheckInById(999L, mockRequest);

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
    controller.createCheckIn(request, mockRequest);
    CheckIn existing = service.getCheckIns().get(0);
    CheckInRequest updateRequest = new CheckInRequest(existing.getGoalId(), existing.getCheckInDate(), "Updated note");
    ResponseEntity<?> response = controller.updateCheckIn(existing.getId(), updateRequest, mockRequest);
    assertEquals(200, response.getStatusCode().value());
  }
  /**
   * API test for updateCheckIn with atypical valid input (empty notes)
   */
  @Test
  @DisplayName("API: updateCheckIn atypical valid input (empty notes)")
  public void testUpdateCheckInAPIAtypical() {
    CheckInRequest request = new CheckInRequest(402L, LocalDateTime.now(), "");
    controller.createCheckIn(request, mockRequest);
    CheckIn existing = service.getCheckIns().get(0);
    CheckInRequest updateRequest = new CheckInRequest(existing.getGoalId(), existing.getCheckInDate(), "");
    ResponseEntity<?> response = controller.updateCheckIn(existing.getId(), updateRequest, mockRequest);
    assertEquals(200, response.getStatusCode().value());
  }

  /**
   * API test for updateCheckIn with invalid input (nonexistent ID)
   */
  @Test
  @DisplayName("API: updateCheckIn invalid input (nonexistent ID)")
  public void testUpdateCheckInAPIInvalid() {
    CheckInRequest updateRequest = new CheckInRequest(999L, LocalDateTime.now(), "No such entry");
    ResponseEntity<?> response = controller.updateCheckIn(9999L, updateRequest, mockRequest);
    assertEquals(404, response.getStatusCode().value());
  }
}

