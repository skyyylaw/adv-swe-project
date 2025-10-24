package com.ontracked.service;

import com.ontracked.model.Goal;
import com.ontracked.model.GoalStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GoalServiceTest {

  private static final Path CSV_PATH = Path.of("goals.csv");

  @BeforeEach
  void cleanBefore() throws Exception {
    Files.deleteIfExists(CSV_PATH);
  }

  @AfterEach
  void cleanAfter() throws Exception {
    Files.deleteIfExists(CSV_PATH);
  }

  @Test
  void loadGoals_whenFileMissing_returnsEmptyList() {
    GoalService svc = new GoalService();
    List<Goal> goals = svc.loadGoals();
    assertNotNull(goals);
    assertTrue(goals.isEmpty(), "Expected empty list when file is absent");
  }

  @Test
  void retrieveGoal_foundAndNotFound_pathsCovered_viaOverriddenLoad() {
    // Override loadGoals to avoid file parsing and to hit both branches
    Goal g1 = new Goal("owner-1");
    g1.setId("id-1");
    Goal g2 = new Goal("owner-2");
    g2.setId("id-2");

    GoalService svc = new GoalService() {
      @Override
      public List<Goal> loadGoals() {
        return List.of(g1, g2);
      }
    };

    Goal found = svc.retrieveGoal("id-2");
    assertNotNull(found);
    assertEquals("id-2", found.getId());

    Goal missing = svc.retrieveGoal("nope");
    assertNull(missing, "Expected null when id is not present");
  }

  @Test
  void saveGoals_writesHeaderOnce_andAppendsWithoutDuplicateHeader_andEscapesCsv() throws Exception {
    GoalService svc = new GoalService();

    // Build a Goal with fields that exercise CSV escaping on title/description
    Goal g = new Goal("owner-123");
    g.setId("goal-1");
    g.setParentId(null); // exercises nullToEmpty
    g.setTitle("Title with, comma and \"quote\"");
    g.setDescription("Desc says \"hello, world\"");
    g.setDueDate(null);
    // Use any existing status to avoid model validation; if your model requires non-null:
    g.setStatus(GoalStatus.ACTIVE); // adjust if your enum differs
    g.setLatestPercentage(42);

    // First write: header must be added
    svc.saveGoals(List.of(g));
    assertTrue(Files.exists(CSV_PATH), "goals.csv should be created");

    List<String> lines1 = Files.readAllLines(CSV_PATH);
    assertFalse(lines1.isEmpty());
    assertEquals(
            "id,ownerId,parentId,title,description,dueDate,status,latestPercentage,createdAt,updatedAt,versionNumber",
            lines1.get(0),
            "Header must be written exactly once on first write"
    );
    assertEquals(2, lines1.size(), "Expect header + one data row after first save");

    String row = lines1.get(1);
    // RFC4180 escaping: values that may contain commas/quotes are quoted, quotes doubled
    assertTrue(row.contains("\"Title with, comma and \"\"quote\"\"\""), "Title should be CSV-escaped");
    assertTrue(row.contains("\"Desc says \"\"hello, world\"\"\""), "Description should be CSV-escaped");

    // Second write with an empty list: should not duplicate header
    svc.saveGoals(List.of());
    List<String> lines2 = Files.readAllLines(CSV_PATH);
    assertEquals(2, lines2.size(), "Second save without rows should not duplicate header");

    // Third write appending another row: header still single
    Goal g2 = new Goal("owner-456");
    g2.setId("goal-2");
    g2.setTitle("Plain");
    g2.setDescription("No commas");
    g2.setStatus(GoalStatus.ACTIVE);
    g2.setLatestPercentage(0);

    svc.saveGoals(List.of(g2));
    List<String> lines3 = Files.readAllLines(CSV_PATH);
    assertEquals(3, lines3.size(), "Append should add exactly one new data line");
    assertEquals(
            "id,ownerId,parentId,title,description,dueDate,status,latestPercentage,createdAt,updatedAt,versionNumber",
            lines3.get(0),
            "Header must not be duplicated on append"
    );
  }

  @Test
  void loadGoals_malformedRow_isIgnored() throws Exception {
    // Create a file with header + a malformed row (< 11 columns)
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_PATH.toFile()))) {
      bw.write("id,ownerId,parentId,title,description,dueDate,status,latestPercentage,createdAt,updatedAt,versionNumber");
      bw.newLine();
      bw.write("too,few,columns");
      bw.newLine();
    }
    assertTrue(new File("goals.csv").exists());

    GoalService svc = new GoalService();
    List<Goal> goals = svc.loadGoals();
    // Malformed row is skipped → 0 parsed goals
    assertNotNull(goals);
    assertTrue(goals.isEmpty(), "Malformed rows should be ignored");
  }
}
