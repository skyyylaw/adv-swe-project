package com.ontracked.service;

import java.io.*;
import java.util.*;
import com.ontracked.model.Goal;
import com.ontracked.model.GoalStatus;
import org.springframework.stereotype.Service;

@Service
public class GoalService {
  private static final String FILE_PATH = "goals.csv";

  public GoalService() {
    // constructor logic if needed
  }

  public static void saveGoals(List<Goal> goals) {
    saveGoalsToCsv(goals);
  }

  public static List<Goal> loadGoals(){
    return loadGoalsFromCsv();
  }

  public static void databaseSetUp() {

  }

  public static void saveGoalsToDatabase(List<Goal> goals) {

  }

  public static Goal retrieveGoal(String id) {
    List<Goal> allGoals = loadGoals();
    for (Goal g : allGoals) {
      if (g.getId().equals(id)) {
        return g;
      }
    }
    return null;
  }

  public static void saveGoalsToCsv(List<Goal> goals) {
    File file = new File(FILE_PATH);
    boolean append = file.exists() && file.length() > 0;

    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
      if (!append) {
        writer.println("id,ownerId,parentId,title,description,dueDate,status,latestPercentage,createdAt,updatedAt,versionNumber");
      }

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


  public static List<Goal> loadGoalsFromCsv() {
    List<Goal> goals = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
      reader.readLine(); // skip header
      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = splitCsv(line);
        if (parts.length < 11) continue;

        Goal g = new Goal(parts[1]); // ownerId
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
      return List.of();
    }
    return goals;
  }

  // --- Utility helpers ---
  private static String nullToEmpty(String s) { return s == null ? "" : s.replace(",", " "); }
  private static String emptyToNull(String s) { return s == null || s.isEmpty() ? null : s; }

  private static String escapeCsv(String s) {
    if (s == null) return "";
    return '"' + s.replace("\"", "\"\"") + '"'; // handle commas/quotes safely
  }

  private static String[] splitCsv(String line) {
    List<String> result = new ArrayList<>();
    boolean inQuotes = false;
    StringBuilder sb = new StringBuilder();
    for (char c : line.toCharArray()) {
      if (c == '"') inQuotes = !inQuotes;
      else if (c == ',' && !inQuotes) {
        result.add(sb.toString().replace("\"\"", "\""));
        sb.setLength(0);
      } else sb.append(c);
    }
    result.add(sb.toString());
    return result.toArray(new String[0]);
  }

//  public static void main(String[] args) {
//    Goal g1 = new Goal("user123");
//    g1.setTitle("Finish MVP");
//    g1.setDescription("Implement core backend logic");
//    g1.setDueDate("2025-11-10");
//
//    Goal g2 = new Goal("user123");
//    g2.setTitle("Ship beta");
//    g2.setDescription("QA + deployment");
//
//    List<Goal> goals = List.of(g1, g2);
//    saveGoalsToCsv(goals);
//
//    System.out.println("Saved goals to CSV.");
//
//    List<Goal> loaded = loadGoalsFromCsv();
//    System.out.println("Loaded goals:");
//    loaded.forEach(System.out::println);
//  }

}
