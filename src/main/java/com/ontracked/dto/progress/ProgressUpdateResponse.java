package com.ontracked.dto.progress;

import models.ProgressUpdate;

import java.time.Instant;

/**
 * Response DTO for progress update data
 */
public class ProgressUpdateResponse {

    private Long id;
    private Long goalId;
    private Integer percentComplete;
    private String note;
    private Instant timestamp;



    public ProgressUpdateResponse() {
    }

    public ProgressUpdateResponse(Long id, Long goalId, Integer percentComplete, String note, Instant timestamp) {
        this.id = id;
        this.goalId = goalId;
        this.percentComplete = percentComplete;
        this.note = note;
        this.timestamp = timestamp;
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


    public Integer getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(Integer percentComplete) {
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

    /**
     * Creates a ProgressUpdateResponse from a ProgressUpdate entity
     */
    public static ProgressUpdateResponse from(ProgressUpdate entity) {

        if (entity == null) {
            return null;
        }


        return new ProgressUpdateResponse(
                entity.getId(),
                entity.getGoalId(),
                entity.getPercentComplete(),
                entity.getNote(),
                entity.getTimestamp()
        );
    }
}
