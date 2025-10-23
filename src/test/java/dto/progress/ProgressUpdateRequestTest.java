package dto.progress;

import models.ProgressUpdate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ProgressUpdateRequest DTO
 */
class ProgressUpdateRequestTest {

    @Test
    void testConstructorWithGoalAndPercent() {
        ProgressUpdateRequest request = new ProgressUpdateRequest(1L, 50);
        assertEquals(1L, request.getGoalId());
        assertEquals(50, request.getPercentComplete());
        assertNull(request.getNote());
    }

    @Test
    void testConstructorWithNote() {
        ProgressUpdateRequest request = new ProgressUpdateRequest(2L, 75, "making progress");
        assertEquals("making progress", request.getNote());
    }

    @Test
    void testSetters() {
        ProgressUpdateRequest request = new ProgressUpdateRequest();
        request.setGoalId(100L);
        request.setPercentComplete(50);
        request.setNote("test");

        assertEquals(100L, request.getGoalId());
        assertEquals(50, request.getPercentComplete());
        assertEquals("test", request.getNote());
    }

    @Test
    void testPercentCompleteZero() {
        ProgressUpdateRequest request = new ProgressUpdateRequest();
        request.setPercentComplete(0);
        assertEquals(0, request.getPercentComplete());
    }

    @Test
    void testPercentCompleteHundred() {
        ProgressUpdateRequest request = new ProgressUpdateRequest();
        request.setPercentComplete(100);
        assertEquals(100, request.getPercentComplete());
    }

    @Test
    void testToEntity() {
        ProgressUpdateRequest request = new ProgressUpdateRequest(5L, 60, "note");
        ProgressUpdate entity = request.toEntity();

        assertNotNull(entity);
        assertEquals(5L, entity.getGoalId());
        assertEquals(60, entity.getPercentComplete());
        assertEquals("note", entity.getNote());
    }

    @Test
    void testToEntityWithoutNote() {
        ProgressUpdateRequest request = new ProgressUpdateRequest(10L, 25);
        ProgressUpdate entity = request.toEntity();

        assertNull(entity.getNote());
    }
}
