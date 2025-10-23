package com.ontracked.model;

import java.time.Instant;

/**
 * Represents a progress update for a goal.
 *
 */
public class ProgressUpdate {

    private Long id;
    private Long goalId;
    private Integer percentComplete;
    private String note;
    private Instant timestamp;
    private Instant createdAt;
    private Instant updatedAt;
    private Integer version;

    /**
     * Default constructor
     */
    public ProgressUpdate() {
        this.timestamp = Instant.now();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.version = 0;
    }

    /**
     * Constructor with required fields
     */
    public ProgressUpdate(Long goalId, Integer percentComplete) {
        this();
        setGoalId(goalId);
        setPercentComplete(percentComplete);
    }

    /**
     * Constructor with all fields
     */
    public ProgressUpdate(Long goalId, Integer percentComplete, String note) {
        this(goalId, percentComplete);
        this.note = note;
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
        if (goalId == null) {
            throw new IllegalArgumentException("goalId cannot be null");
        }
        this.goalId = goalId;
    }

    public Integer getPercentComplete() {
        return percentComplete;
    }



    public void setPercentComplete(Integer percentComplete) {
        if (percentComplete == null) {
            throw new IllegalArgumentException("percentComplete cannot be null");
        }

        if (percentComplete < 0 || percentComplete > 100) {
            throw new IllegalArgumentException("percentComplete must be between 0 and 100");
        }
        this.percentComplete = percentComplete;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }


    public Instant getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }



    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Updates the updatedAt timestamp to current time
     */
    public void touchUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
