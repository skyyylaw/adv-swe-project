package models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ProgressUpdate model
 */
class ProgressUpdateTest {

    private ProgressUpdate progressUpdate;

    @BeforeEach
    void setUp() {
        progressUpdate = new ProgressUpdate();
    }

    @Test
    void testConstructorSetsDefaults() {
        ProgressUpdate update = new ProgressUpdate();
        assertNotNull(update.getTimestamp());
        assertEquals(0, update.getVersion());
    }

    @Test
    void testConstructorWithGoalAndPercent() {
        ProgressUpdate update = new ProgressUpdate(1L, 50);
        assertEquals(1L, update.getGoalId());
        assertEquals(50, update.getPercentComplete());
    }

    @Test
    void testConstructorWithNote() {
        ProgressUpdate update = new ProgressUpdate(1L, 75, "halfway there");
        assertEquals("halfway there", update.getNote());
    }

    @Test
    void testSetGoalIdValid() {
        progressUpdate.setGoalId(100L);
        assertEquals(100L, progressUpdate.getGoalId());
    }

    @Test
    void testSetGoalIdThrowsOnNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            progressUpdate.setGoalId(null);
        });
    }

    @Test
    void testSetPercentComplete() {
        progressUpdate.setPercentComplete(50);
        assertEquals(50, progressUpdate.getPercentComplete());
    }

    @Test
    void testSetPercentCompleteZero() {
        progressUpdate.setPercentComplete(0);
        assertEquals(0, progressUpdate.getPercentComplete());
    }

    @Test
    void testSetPercentCompleteHundred() {
        progressUpdate.setPercentComplete(100);
        assertEquals(100, progressUpdate.getPercentComplete());
    }

    @Test
    void testPercentCompleteRejectsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            progressUpdate.setPercentComplete(-1);
        });
    }

    @Test
    void testPercentCompleteRejectsOverHundred() {
        assertThrows(IllegalArgumentException.class, () -> {
            progressUpdate.setPercentComplete(101);
        });
    }

    @Test
    void testPercentCompleteRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            progressUpdate.setPercentComplete(null);
        });
    }

    @Test
    void testSetNote() {
        progressUpdate.setNote("finished the first part");
        assertEquals("finished the first part", progressUpdate.getNote());
    }

    @Test
    void testNoteCanBeNull() {
        progressUpdate.setNote(null);
        assertNull(progressUpdate.getNote());
    }
}
