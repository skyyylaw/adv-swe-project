package com.ontracked.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Goal} domain model.
 *
 * <p>This test suite ensures the following:
 * <ul>
 *   <li>Constructors enforce validation and defaults correctly.</li>
 *   <li>Setters perform clamping, null/blank normalization, and version bumps.</li>
 *   <li>List fields (childrenId) are defensive, unmodifiable, and sanitized.</li>
 *   <li>Timestamps (createdAt/updatedAt) follow logical ordering rules.</li>
 *   <li>Equality, hashCode, and toString behave deterministically by ID.</li>
 * </ul>
 *
 * These tests are purely at the model level (no Spring context).
 */
class GoalTest {

  private Goal goal;

  @BeforeEach
  void setUp() {
    goal = new Goal("Sky");
  }

  // ------------------------------------------------------------
  // Constructor behavior
  // ------------------------------------------------------------

  /** Verifies that the minimal constructor sets defaults properly. */
  @Test
  void testMinimalConstructorSetsDefaults() {
    assertNotNull(goal.getId());
    assertEquals("Sky", goal.getOwnerId());
    assertEquals(GoalStatus.ACTIVE, goal.getStatus());
    assertEquals(0, goal.getLatestPercentage());
    assertNotNull(goal.getCreatedAt());
    assertNotNull(goal.getUpdatedAt());
    assertEquals(1, goal.getVersionNumber());
    assertTrue(goal.getChildrenId().isEmpty());
  }

  /** Verifies that the full constructor applies validation, clamping, and sanitization. */
  @Test
  void testFullConstructorWithValidData() {
    Instant now = Instant.now();
    List<String> children = Arrays.asList("a", "b", "a", "", null);

    Goal g = new Goal(
            UUID.randomUUID().toString(),
            "Alice",
            "",
            children,
            "Title",
            "Desc",
            "2025-10-01",
            GoalStatus.COMPLETED,
            150,              // should be clamped to 100
            now,
            now,
            5
    );

    assertEquals("Alice", g.getOwnerId());
    assertEquals(GoalStatus.COMPLETED, g.getStatus());
    assertEquals(100, g.getLatestPercentage());          // clamped to 100
    assertEquals(5, g.getVersionNumber());
    assertEquals(List.of("a", "b"), g.getChildrenId());  // sanitized duplicates/nulls removed
    assertEquals("2025-10-01", g.getDueDate());
  }

  /** Ensures invalid or blank owner IDs throw exceptions. */
  @Test
  void testConstructorRejectsInvalidOwnerId() {
    assertThrows(IllegalArgumentException.class, () -> new Goal(""));
    assertThrows(IllegalArgumentException.class, () -> new Goal("  "));
  }

  /** Ensures invalid date formats trigger an exception. */
  @Test
  void testConstructorRejectsInvalidDates() {
    Instant now = Instant.now();
    assertThrows(IllegalArgumentException.class, () -> new Goal(
            "id", "Sky", null, null, null, null, "invalid-date",
            GoalStatus.ACTIVE, 0, now, now, 1));
  }

  /** Ensures updatedAt cannot precede createdAt. */
  @Test
  void testUpdatedAtCannotBeBeforeCreatedAt() {
    Instant now = Instant.now();
    Instant past = now.minusSeconds(100);
    assertThrows(IllegalArgumentException.class, () -> new Goal(
            "id", "Sky", null, null, null, null, null,
            GoalStatus.ACTIVE, 0, now, past, 1));
  }

  // ------------------------------------------------------------
  // Setter and versioning logic
  // ------------------------------------------------------------

  /** Confirms that mutator calls increment version number and update timestamp. */
  @Test
  void testSettersBumpVersion() {
    int originalVersion = goal.getVersionNumber();
    goal.setTitle("New Title");
    assertEquals(originalVersion + 1, goal.getVersionNumber());
    goal.setDescription("Updated");
    assertTrue(goal.getVersionNumber() > originalVersion);
  }

  /** Verifies clamping of percentage to [0, 100]. */
  @Test
  void testSetLatestPercentageClamps() {
    goal.setLatestPercentage(-10);
    assertEquals(0, goal.getLatestPercentage());
    goal.setLatestPercentage(150);
    assertEquals(100, goal.getLatestPercentage());
  }

  /** Validates proper date parsing and rejection of malformed input. */
  @Test
  void testSetDueDateValidation() {
    goal.setDueDate("2025-10-10");
    assertEquals("2025-10-10", goal.getDueDate());
    assertThrows(IllegalArgumentException.class, () -> goal.setDueDate("not-a-date"));
  }

  /** Verifies blank ownerId updates are rejected. */
  @Test
  void testSetOwnerIdRejectsBlank() {
    assertThrows(IllegalArgumentException.class, () -> goal.setOwnerId(""));
  }

  /** Ensures child ID lists are cleaned up when set. */
  @Test
  void testSetChildrenIdRemovesNullsAndDuplicates() {
    goal.setChildrenId(Arrays.asList("1", "1", " ", null, "2"));
    assertEquals(List.of("1", "2"), goal.getChildrenId());
  }

  // ------------------------------------------------------------
  // Children management
  // ------------------------------------------------------------

  /** Tests add/remove child operations and duplicate prevention. */
  @Test
  void testAddAndRemoveChild() {
    assertTrue(goal.addChild("child1"));
    assertFalse(goal.addChild("child1")); // duplicate should fail
    assertTrue(goal.removeChild("child1"));
    assertFalse(goal.removeChild("child1")); // already removed
  }

  /** Verifies blank or null child IDs are rejected. */
  @Test
  void testAddChildRejectsBlank() {
    assertThrows(IllegalArgumentException.class, () -> goal.addChild(" "));
  }

  // ------------------------------------------------------------
  // Timestamp validation
  // ------------------------------------------------------------

  /** Ensures timestamp logic enforces correct chronological order. */
  @Test
  void testSetCreatedAtAndUpdatedAtValidation() {
    Instant base = Instant.now();
    goal.setCreatedAt(base.minusSeconds(5));
    goal.setUpdatedAt(base);
    assertTrue(goal.getUpdatedAt().isAfter(goal.getCreatedAt()));

    // Now try setting updatedAt before createdAt → should fail
    assertThrows(IllegalArgumentException.class,
            () -> goal.setUpdatedAt(base.minusSeconds(100)));
  }

  // ------------------------------------------------------------
  // Equality, hashCode, toString
  // ------------------------------------------------------------

  /** Equality and hashCode should depend only on ID. */
  @Test
  void testEqualityAndHashCodeBasedOnId() {
    Goal g1 = new Goal("Alice");
    Goal g2 = new Goal("Alice");
    g2.setId(g1.getId());
    assertEquals(g1, g2);
    assertEquals(g1.hashCode(), g2.hashCode());
  }

  /** toString() should contain key identifying fields for debugging. */
  @Test
  void testToStringContainsKeyFields() {
    goal.setTitle("Focus");
    goal.setDueDate("2025-12-01");
    String str = goal.toString();
    assertTrue(str.contains("Focus"));
    assertTrue(str.contains("2025-12-01"));
    assertTrue(str.contains("ACTIVE"));
  }

  // ------------------------------------------------------------
  // Defensive programming checks
  // ------------------------------------------------------------

  /** Ensures children list is exposed as unmodifiable. */
  @Test
  void testGetChildrenIdReturnsUnmodifiableList() {
    goal.addChild("x");
    List<String> children = goal.getChildrenId();
    assertThrows(UnsupportedOperationException.class, () -> children.add("new"));
  }

  /** Confirms order preservation and whitespace trimming in sanitization. */
  @Test
  void testSanitizeChildrenPreservesOrderAndRemovesBlanks() {
    List<String> input = Arrays.asList(" a ", "b", "a", " ", null, "c");
    goal.setChildrenId(input);
    assertEquals(List.of("a", "b", "c"), goal.getChildrenId());
  }

  /** Confirms touchUpdatedAt properly bumps version and timestamp. */
  @Test
  void testTouchUpdatedAtBumpsVersionAndTimestamp() {
    int oldVersion = goal.getVersionNumber();
    Instant oldTime = goal.getUpdatedAt();
    goal.touchUpdatedAt();
    assertTrue(goal.getVersionNumber() > oldVersion);
    assertTrue(goal.getUpdatedAt().isAfter(oldTime));
  }

  /** Version number must never go below 1. */
  @Test
  void testSetVersionNumberDoesNotDecrease() {
    goal.setVersionNumber(0);
    assertEquals(1, goal.getVersionNumber());
  }

  /** Blank strings should normalize to null on input. */
  @Test
  void testBlankInputsBecomeNull() {
    goal.setTitle(" ");
    goal.setDescription(" ");
    goal.setParentId(" ");
    assertNull(goal.getTitle());
    assertNull(goal.getDescription());
    assertNull(goal.getParentId());
  }

  /** Null ownerId input should trigger validation failure. */
  @Test
  void testRequireNonBlankThrowsForNull() {
    assertThrows(IllegalArgumentException.class, () -> new Goal(null));
  }

  /** Tests edge clamping of percentage boundaries 0 and 100. */
  @Test
  void testClampingPercentageEdgeCases() {
    goal.setLatestPercentage(0);
    assertEquals(0, goal.getLatestPercentage());
    goal.setLatestPercentage(100);
    assertEquals(100, goal.getLatestPercentage());
  }
}
