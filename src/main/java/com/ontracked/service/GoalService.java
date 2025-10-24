package com.ontracked.service;

import com.ontracked.model.Goal;
import com.ontracked.model.GoalStatus;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * Service layer for managing {@link Goal} persistence and retrieval.
 *
 * <p>This implementation uses a simple CSV file as a lightweight local datastore.
 * It provides methods to:
 * <ul>
 *   <li>Load all goals from the CSV file</li>
 *   <li>Retrieve a goal by ID</li>
 *   <li>Save one or more goals back to the CSV</li>
 * </ul>
 *
 * <p>In a production setup, this would typically be replaced
 * with a database-backed repository or ORM integration.
 */
@Service
public class GoalService {

  /** Local file path used for storing serialized goal data. */
  private static final String FILE_PATH = "goals.csv";

  /**
   * Loads all goals currently stored in the CSV file.
   *
   * @return list of all {@link Goal} objects; empty list if file not found or unreadable
   */
  public List<Goal> loadGoals() {
    return loadGoalsFromCsv();
  }

  /**
   * Retrieves a specific goal by its unique identifier.
   *
   * @param id the goal's UUID string
   * @return the matching {@link Goal}, or {@code null} if not found
   */
  public Goal retrieveGoal(String id) {
    List<Goal> allGoals = loadGoals();
    for (Goal g : allGoals) {
      if (g.getId().equals(id)) {
        return g;
      }
    }
    return null;
  }

  /**
   * Persists one or more goals to the CSV file.
   * Appends new rows to the existing file if it already exists.
   *
   * @param goals list of goals to save
   * @throws RuntimeException if file write fails
   */
  public void saveGoals(List<Goal> goals) {
    saveGoalsToCsv(goals);
  }

  /**
   * Writes goals to a local CSV file. If the file exists and is non-empty,
   * new entries are appended; otherwise, a header row is written first.
   */
  private void saveGoalsToCsv(List<Goal> goals) {
    File file = new File(FILE_PATH);
    boolean append = file.exists() && file.length() > 0;

    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
      // Write header only once
      if (!append) {
        writer.println("id,ownerId,parentId,title,description,dueDate,status,latestPercentage,createdAt,updatedAt,versionNumber");
      }

      // Serialize each Goal into a CSV line
      for (Goal g : goals) {
        writer.printf("%s,%s,%s,%s,%s,%s,%s,%d,%s,%s,%d%n",
                nullToEmpty(g.getId()),
                nullToEmpty(g.getOwnerId()),
                nullToEmpty(g.getParentId()),
                escapeCsv(g.getTitle()),
                escapeCsv(g.getDescription()),
                nullToEmpty(g.getDueDate()),
                g.getStatus(),
                g.getLatestPercentage(),
                g.getCreatedAt(),
                g.getUpdatedAt(),
                g.getVersionNumber());
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to write CSV", e);
    }
  }

  /**
   * Reads and parses the goals stored in {@code goals.csv}.
   * Lines are split using custom CSV parsing to support quoted fields with commas.
   *
   * @return list of loaded {@link Goal} objects; empty list if I/O fails
   */
  private List<Goal> loadGoalsFromCsv() {
    List<Goal> goals = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      reader.readLine(); // skip header row
      String line;

      while ((line = reader.readLine()) != null) {
        String[] parts = splitCsv(line);
        if (parts.length < 11) {
          continue;
        } // ignore malformed rows

        // Reconstruct Goal from CSV columns
        Goal g = new Goal(parts[1]);  // ownerId
        g.setId(parts[0]);
        g.setParentId(emptyToNull(parts[2]));
        g.setTitle(emptyToNull(parts[3]));
        g.setDescription(emptyToNull(parts[4]));
        g.setDueDate(emptyToNull(parts[5]));
        g.setStatus(GoalStatus.valueOf(parts[6]));
        g.setLatestPercentage(Integer.parseInt(parts[7]));
        goals.add(g);
      }
    } catch (IOException e) {
      // File missing or unreadable — treat as empty dataset
      return List.of();
    }
    return goals;
  }

  // ------------------------------------------------------------------------
  // Helper methods
  // ------------------------------------------------------------------------

  /** Returns an empty string if {@code s} is null; replaces commas with spaces. */
  private static String nullToEmpty(String s) {
    return s == null ? "" : s.replace(",", " ");
  }

  /** Returns null if {@code s} is null or empty, otherwise returns s as-is. */
  private static String emptyToNull(String s) {
    return (s == null || s.isEmpty()) ? null : s;
  }

  /**
   * Escapes commas and quotes for CSV-safe output.
   * Double-quotes inside values are doubled for RFC4180 compliance.
   */
  private static String escapeCsv(String s) {
    if (s == null) {
      return "";
    }
    return '"' + s.replace("\"", "\"\"") + '"';
  }

  /**
   * Splits a single CSV line into its fields,
   * preserving commas inside quoted sections.
   *
   * @param line a line of CSV-formatted text
   * @return array of field values with quotes stripped
   */
  private static String[] splitCsv(String line) {
    List<String> result = new ArrayList<>();
    boolean inQuotes = false;
    StringBuilder sb = new StringBuilder();

    for (char c : line.toCharArray()) {
      if (c == '"') {
        inQuotes = !inQuotes;
      } else if (c == ',' && !inQuotes) {
        result.add(sb.toString().replace("\"\"", "\""));
        sb.setLength(0);
      } else {
        sb.append(c);
      }
    }
    result.add(sb.toString());
    return result.toArray(new String[0]);
  }
}
