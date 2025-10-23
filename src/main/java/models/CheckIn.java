package models;

import java.time.LocalDateTime;

public class CheckIn {
    private Long id;
    private Long goalId;
    private LocalDateTime checkInDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int version;

  

    // Default (no-args) constructor
    public CheckIn() {
    }

    // All-args constructor
    public CheckIn(Long id, Long goalId, LocalDateTime checkInDate, String notes,
                   LocalDateTime createdAt, LocalDateTime updatedAt, int version) {
        this.id = id;
        this.goalId = goalId;
        this.checkInDate = checkInDate;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }

 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public LocalDateTime getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDateTime checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
