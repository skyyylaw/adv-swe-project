package com.ontracked.dto.progress;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import models.ProgressUpdate;

/**
 * Request DTO for creating or updating a progress update
 */
public class ProgressUpdateRequest {

    @NotNull(message = "goalId is required")
    private Long goalId;

    @NotNull(message = "percentComplete is required")
    @Min(value = 0, message = "percentComplete must be at least 0")
    @Max(value = 100, message = "percentComplete must be at most 100")
    private Integer percentComplete;

    private String note;



    public ProgressUpdateRequest() {
    }

    public ProgressUpdateRequest(Long goalId, Integer percentComplete) {
        this.goalId = goalId;
        this.percentComplete = percentComplete;
    }

    public ProgressUpdateRequest(Long goalId, Integer percentComplete, String note) {
        this.goalId = goalId;
        this.percentComplete = percentComplete;
        this.note = note;
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

    /**
     * Converts this DTO to a ProgressUpdate entity
     */
    public ProgressUpdate toEntity() {
        return new ProgressUpdate(this.goalId, this.percentComplete, this.note);
    }
}
