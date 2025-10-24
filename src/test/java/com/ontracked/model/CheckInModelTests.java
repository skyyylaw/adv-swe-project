package com.ontracked.model;


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

public class CheckInModelTests {

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
}
