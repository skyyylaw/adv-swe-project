package com.ontracked.service;

import com.ontracked.model.CheckIn;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckInServiceTest {

    // Minimal subclass that disables file I/O
    static class TestableCheckInService extends CheckInService {
        @Override
        protected void loadFromCsv() {
            // Skip file loading for tests
        }

        @Override
        protected void saveToCsv() {
            // Skip saving to CSV for tests
        }
    }

    private TestableCheckInService service;

    @BeforeEach
    void setUp() {
        service = new TestableCheckInService();
    }


    /**
     * Basic tests for addCheckIn and updateCheckIn methods
     */
    @Test
    void testAddCheckIn_AssignsFields() {
        CheckIn c = new CheckIn();
        c.setGoalId(5L);
        c.setNotes("Initial");

        service.addCheckIn(c);
        List<CheckIn> list = service.getCheckIns();

        assertEquals(1, list.size());
        CheckIn added = list.get(0);

        assertNotNull(added.getId(), "ID should be assigned automatically");
        assertNotNull(added.getCheckInDate(), "CheckInDate should be set automatically");
        assertNotNull(added.getCreatedAt());
        assertNotNull(added.getUpdatedAt());
        assertEquals(1, added.getVersion());
    }


    /**
     * Tests for updateCheckIn method
     */
    @Test
    void testUpdateCheckIn_UpdatesNotes() {
        CheckIn c = new CheckIn();
        c.setId(1L);
        c.setGoalId(5L);
        c.setNotes("Before");
        service.getCheckIns().add(c);

        CheckIn updated = new CheckIn();
        updated.setId(1L);
        updated.setGoalId(5L);
        updated.setNotes("After");

        service.updateCheckIn(updated);
        CheckIn result = service.getCheckIns().get(0);

        assertEquals("After", result.getNotes());
        assertNotNull(result.getUpdatedAt(), "UpdatedAt should be refreshed");
    }

    /**
     * Tests that updateCheckIn does not throw when no matching ID is found
     */
    @Test
    void testUpdateCheckIn_NoMatch_DoesNotThrow() {
        CheckIn c = new CheckIn();
        c.setId(1L);
        c.setNotes("Original");
        service.getCheckIns().add(c);

        CheckIn nonExistent = new CheckIn();
        nonExistent.setId(99L);
        nonExistent.setNotes("Doesn't exist");

        assertDoesNotThrow(() -> service.updateCheckIn(nonExistent));
        assertEquals("Original", service.getCheckIns().get(0).getNotes());
    }

    /**
     * Tests for generate next id helper method 
     * @throws Exception
     */
    @Test
    void testGenerateNextId_IncrementsMaxId() throws Exception {
        CheckIn a = new CheckIn();
        a.setId(3L);
        CheckIn b = new CheckIn();
        b.setId(8L);
        service.getCheckIns().add(a);
        service.getCheckIns().add(b);

        Method generateNextId = CheckInService.class.getDeclaredMethod("generateNextId");
        generateNextId.setAccessible(true);
        Long nextId = (Long) generateNextId.invoke(service);

        assertEquals(9L, nextId);
    }

    /**
     * Tests for parse long helper method
     * @throws Exception
     */
    @Test
    void testParseLong_WorksForValidAndInvalid() throws Exception {
        Method parseLong = CheckInService.class.getDeclaredMethod("parseLong", String.class);
        parseLong.setAccessible(true);

        assertNull(parseLong.invoke(service, ""));
        assertNull(parseLong.invoke(service, "abc"));
        assertEquals(42L, parseLong.invoke(service, "42"));
    }

    /**
     * Tests for parse date helper method
     * @throws Exception
     */
    @Test
    void testParseDate_WorksForValidAndInvalid() throws Exception {
        Method parseDate = CheckInService.class.getDeclaredMethod("parseDate", String.class);
        parseDate.setAccessible(true);

        assertNull(parseDate.invoke(service, ""));
        assertNull(parseDate.invoke(service, "not-a-date"));
        assertNotNull(parseDate.invoke(service, LocalDateTime.now().toString()));
    }
}