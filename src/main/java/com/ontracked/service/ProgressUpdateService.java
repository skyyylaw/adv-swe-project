package com.ontracked.service;

import com.ontracked.model.ProgressUpdate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for ProgressUpdate operations.
 * Uses CSV file for data storage.
 */
@Service
public class ProgressUpdateService {

    private final String CSV_FILE_PATH = "progressUpdates.csv";
    private List<ProgressUpdate> progressUpdates;

    public ProgressUpdateService() {
        progressUpdates = new ArrayList<>();
        loadFromCsv();
    }


    public List<ProgressUpdate> getAllProgressUpdates() {
        return new ArrayList<>(progressUpdates);
    }


    public ProgressUpdate getProgressUpdateById(Long id) {
        for (ProgressUpdate pu : progressUpdates) {
            if (pu.getId().equals(id)) {
                return pu;
            }
        }
        return null;
    }


    public List<ProgressUpdate> getProgressUpdatesByGoalId(Long goalId) {
        List<ProgressUpdate> result = new ArrayList<>();
        for (ProgressUpdate pu : progressUpdates) {
            if (pu.getGoalId().equals(goalId)) {
                result.add(pu);
            }
        }
        return result;
    }


    public void addProgressUpdate(ProgressUpdate progressUpdate) {
        if (progressUpdate.getId() == null) {
            progressUpdate.setId(generateNextId());
        }


        if (progressUpdate.getTimestamp() == null) {
            progressUpdate.setTimestamp(Instant.now());
        }
        progressUpdate.setCreatedAt(Instant.now());
        progressUpdate.setUpdatedAt(Instant.now());
        if (progressUpdate.getVersion() == null) {
            progressUpdate.setVersion(0);
        }

        progressUpdates.add(progressUpdate);
        saveToCsv();
    }


    public void updateProgressUpdate(ProgressUpdate updatedProgressUpdate) {
        for (int i = 0; i < progressUpdates.size(); i++) {
            ProgressUpdate pu = progressUpdates.get(i);
            if (pu.getId().equals(updatedProgressUpdate.getId())) {
                updatedProgressUpdate.setUpdatedAt(Instant.now());
                progressUpdates.set(i, updatedProgressUpdate);
                saveToCsv();
                return;
            }
        }
        System.err.println("No ProgressUpdate found with ID " + updatedProgressUpdate.getId());
    }


    public boolean deleteProgressUpdate(Long id) {
        boolean removed = progressUpdates.removeIf(pu -> pu.getId().equals(id));
        if (removed) {
            saveToCsv();
        }
        return removed;
    }

    // helper to generate IDs
    private Long generateNextId() {
        if (progressUpdates.isEmpty()) {
            return 1L;
        }
        Long maxId = 0L;
        for (ProgressUpdate pu : progressUpdates) {
            if (pu.getId() != null && pu.getId() > maxId) {
                maxId = pu.getId();
            }
        }
        return maxId + 1;
    }

    // load data from CSV at startup
    protected void loadFromCsv() {
        File file = new File(CSV_FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length < 8) continue;

                try {
                    ProgressUpdate pu = new ProgressUpdate();
                    pu.setId(parts[0].isEmpty() ? null : Long.parseLong(parts[0]));
                    pu.setGoalId(parts[1].isEmpty() ? null : Long.parseLong(parts[1]));
                    pu.setPercentComplete(parts[2].isEmpty() ? null : Integer.parseInt(parts[2]));
                    pu.setNote(parts[3].isEmpty() ? null : parts[3]);
                    pu.setTimestamp(parts[4].isEmpty() ? null : Instant.parse(parts[4]));
                    pu.setCreatedAt(parts[5].isEmpty() ? null : Instant.parse(parts[5]));
                    pu.setUpdatedAt(parts[6].isEmpty() ? null : Instant.parse(parts[6]));
                    pu.setVersion(parts[7].isEmpty() ? null : Integer.parseInt(parts[7]));

                    progressUpdates.add(pu);
                } catch (Exception e) {
                    System.err.println("Error parsing line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    // persist current state back to CSV
    protected void saveToCsv() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE_PATH))) {
            writer.println("id,goalId,percentComplete,note,timestamp,createdAt,updatedAt,version");

            for (ProgressUpdate pu : progressUpdates) {
                writer.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                        pu.getId() != null ? pu.getId() : "",
                        pu.getGoalId() != null ? pu.getGoalId() : "",
                        pu.getPercentComplete() != null ? pu.getPercentComplete() : "",
                        pu.getNote() != null ? pu.getNote() : "",
                        pu.getTimestamp() != null ? pu.getTimestamp() : "",
                        pu.getCreatedAt() != null ? pu.getCreatedAt() : "",
                        pu.getUpdatedAt() != null ? pu.getUpdatedAt() : "",
                        pu.getVersion() != null ? pu.getVersion() : "");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save progress updates to CSV", e);
        }
    }
}
