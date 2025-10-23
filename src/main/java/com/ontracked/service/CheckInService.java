package com.ontracked.service;

import com.ontracked.model.CheckIn;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *  This class defines the Mock API Service mimicking CLIO's database for CheckIns.
 *  It provides useful methods for accessing or modifying check-ins,
 *  loading and saving its data from a CSV file.
 */

@Service
public class CheckInService {

  private final String CSV_FILE_PATH = "localGoalDB.csv"; // resource filename
  private ArrayList<CheckIn> checkIns;


  public CheckInService() {
    checkIns = new ArrayList<>();
    loadFromCsv();
  }

  
  /**
   * Get all stored check-ins.
   * @return A list of all CheckIn objects.
   */
  public ArrayList<CheckIn> getCheckIns() {
    return checkIns;
  }

  /**
   * Add a new CheckIn to the storage.
   * @param newCheckIn The CheckIn object to add.
   */
  public void addCheckIn(CheckIn newCheckIn) {
    if (newCheckIn.getId() == null) {
      newCheckIn.setId(generateNextId());
    }
    if (newCheckIn.getCheckInDate() == null) {
      newCheckIn.setCheckInDate(LocalDateTime.now());
    }
    newCheckIn.setCreatedAt(LocalDateTime.now());
    newCheckIn.setUpdatedAt(LocalDateTime.now());
    newCheckIn.setVersion(1);

    checkIns.add(newCheckIn);
    saveToCsv();
  }

  /**
   * Update an existing CheckIn in the storage.
   * @param updatedCheckIn The CheckIn object with updated data.
   */
  public void updateCheckIn(CheckIn updatedCheckIn) {
    List<CheckIn> tmpList = new ArrayList<>();
    boolean updated = false;

    for (CheckIn checkIn : checkIns) {
      if (checkIn.getId().equals(updatedCheckIn.getId())) {
        updatedCheckIn.setUpdatedAt(LocalDateTime.now());
        tmpList.add(updatedCheckIn);
        updated = true;
      } else {
        tmpList.add(checkIn);
      }
    }

    if (updated) {
      this.checkIns = new ArrayList<>(tmpList);
      saveToCsv();
    } else {
      System.err.println("No CheckIn found with ID " + updatedCheckIn.getId());
    }
  }

  
  public void printCheckIns() {
    checkIns.forEach(System.out::println);
  }

  // Helper methods for CSV loading
  private void loadFromCsv() {
    try {
      URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(CSV_FILE_PATH);
      if (resourceUrl == null) {
        System.err.println("Failed to find " + CSV_FILE_PATH + " in resources.");
        return;
      }

      File file = new File(resourceUrl.toURI());
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        boolean isHeader = true;

        while ((line = reader.readLine()) != null) {
          if (isHeader) {
            isHeader = false;
            continue;
          }

          String[] parts = line.split(",", -1);
          if (parts.length < 4) continue;

          CheckIn checkIn = new CheckIn();
          checkIn.setId(parseLong(parts[0]));
          checkIn.setGoalId(parseLong(parts[1]));
          checkIn.setCheckInDate(parseDate(parts[2]));
          checkIn.setNotes(parts[3]);
          checkIn.setCreatedAt(LocalDateTime.now());
          checkIn.setUpdatedAt(LocalDateTime.now());
          checkIn.setVersion(1);

          checkIns.add(checkIn);
        }

        System.out.println("Successfully loaded check-ins from " + CSV_FILE_PATH);
      }
    } catch (Exception e) {
      System.err.println("Failed to load check-ins: " + e.getMessage());
    }
  }
  // Helper methods for CSV saving
  private void saveToCsv() {
    try {
      URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(CSV_FILE_PATH);
      if (resourceUrl == null) {
        System.err.println("Cannot find " + CSV_FILE_PATH + " to save updates.");
        return;
      }

      File file = new File(resourceUrl.toURI());
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        writer.write("id,goalId,checkInDate,notes\n");
        for (CheckIn c : checkIns) {
          writer.write(String.format("%d,%d,%s,%s\n",
              c.getId(),
              c.getGoalId(),
              c.getCheckInDate() != null ? c.getCheckInDate().toString() : "",
              c.getNotes() != null ? c.getNotes().replace(",", " ") : ""));
        }
      }

      System.out.println("Successfully saved check-ins to " + CSV_FILE_PATH);
    } catch (Exception e) {
      System.err.println("Failed to save check-ins: " + e.getMessage());
    }
  }

  // Helper to generate the next unique ID
  private Long generateNextId() {
    return checkIns.stream()
        .mapToLong(c -> c.getId() != null ? c.getId() : 0)
        .max()
        .orElse(0) + 1;
  }
  

  private Long parseLong(String s) {
    try {
      return (s == null || s.isEmpty()) ? null : Long.parseLong(s.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private LocalDateTime parseDate(String s) {
    try {
      return (s == null || s.isEmpty()) ? null : LocalDateTime.parse(s.trim());
    } catch (Exception e) {
      return null;
    }
  }
}