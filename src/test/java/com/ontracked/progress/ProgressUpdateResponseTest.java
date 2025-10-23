package com.ontracked.progress;

import com.ontracked.dto.progress.ProgressUpdateResponse;
import com.ontracked.model.ProgressUpdate;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

/**
 * Tests for ProgressUpdateResponse DTO
 */
class ProgressUpdateResponseTest {

    @Test
    void testConstructor() {
        Instant now = Instant.now();
        ProgressUpdateResponse response = new ProgressUpdateResponse(1L, 10L, 50, "note", now);

        assertEquals(1L, response.getId());
        assertEquals(10L, response.getGoalId());
        assertEquals(50, response.getPercentComplete());
        assertEquals("note", response.getNote());
        assertEquals(now, response.getTimestamp());
    }

    @Test
    void testSetters() {
        ProgressUpdateResponse response = new ProgressUpdateResponse();
        Instant now = Instant.now();

        response.setId(123L);
        response.setGoalId(456L);
        response.setPercentComplete(75);
        response.setNote("updated");
        response.setTimestamp(now);

        assertEquals(123L, response.getId());
        assertEquals(456L, response.getGoalId());
        assertEquals(75, response.getPercentComplete());
        assertEquals("updated", response.getNote());
        assertEquals(now, response.getTimestamp());
    }

    @Test
    void testFromEntity() {
        ProgressUpdate entity = new ProgressUpdate(5L, 60, "done");
        entity.setId(100L);

        ProgressUpdateResponse response = ProgressUpdateResponse.from(entity);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(5L, response.getGoalId());
        assertEquals(60, response.getPercentComplete());
        assertEquals("done", response.getNote());
    }

    @Test
    void testFromEntityWithoutNote() {
        ProgressUpdate entity = new ProgressUpdate(10L, 25);
        entity.setId(200L);

        ProgressUpdateResponse response = ProgressUpdateResponse.from(entity);

        assertNull(response.getNote());
    }

    @Test
    void testFromNullEntity() {
        ProgressUpdateResponse response = ProgressUpdateResponse.from(null);
        assertNull(response);
    }

    @Test
    void testPercentCompleteZero() {
        ProgressUpdate entity = new ProgressUpdate(7L, 0);
        entity.setId(300L);

        ProgressUpdateResponse response = ProgressUpdateResponse.from(entity);
        assertEquals(0, response.getPercentComplete());
    }

    @Test
    void testPercentCompleteHundred() {
        ProgressUpdate entity = new ProgressUpdate(8L, 100);
        entity.setId(400L);

        ProgressUpdateResponse response = ProgressUpdateResponse.from(entity);
        assertEquals(100, response.getPercentComplete());
    }
}
