package com.ontracked.dto.goal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request payload to update an existing Goal (partial update). */
public class UpdateGoalRequest {
  @NotBlank private String id;                   // the goal to update (required)
  private String parentId;                       // optional
  @Size(max = 200) private String title;         // optional
  @Size(max = 2000) private String description;  // optional
  /** ISO-8601 yyyy-MM-dd; optional. */
  private String dueDate;
  /** optional status change */
  private models.GoalStatus status;
  /** optional progress update; 0..100 */
  @Min(0) @Max(100) private Integer latestPercentage;
  /** optional optimistic-locking guard */
  private Integer expectedVersion;

  public UpdateGoalRequest() {}

  public UpdateGoalRequest(
          String id, String parentId, String title, String description, String dueDate,
          models.GoalStatus status, Integer latestPercentage, Integer expectedVersion) {
    this.id = id;
    this.parentId = parentId;
    this.title = title;
    this.description = description;
    this.dueDate = dueDate;
    this.status = status;
    this.latestPercentage = latestPercentage;
    this.expectedVersion = expectedVersion;
  }

  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  public String getParentId() { return parentId; }
  public void setParentId(String parentId) { this.parentId = parentId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getDueDate() { return dueDate; }
  public void setDueDate(String dueDate) { this.dueDate = dueDate; }
  public models.GoalStatus getStatus() { return status; }
  public void setStatus(models.GoalStatus status) { this.status = status; }
  public Integer getLatestPercentage() { return latestPercentage; }
  public void setLatestPercentage(Integer latestPercentage) { this.latestPercentage = latestPercentage; }
  public Integer getExpectedVersion() { return expectedVersion; }
  public void setExpectedVersion(Integer expectedVersion) { this.expectedVersion = expectedVersion; }
}

