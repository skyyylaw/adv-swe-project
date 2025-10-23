package dto.goal;

import java.time.Instant;
import java.util.List;

/** Response shape for a Goal. */
public class GoalResponse {
  private String id;
  private String ownerId;
  private String parentId;
  private List<String> childrenIds;
  private String title;
  private String description;
  private String dueDate;                 // ISO yyyy-MM-dd or null
  private models.GoalStatus status;
  private int latestPercentage;
  private Instant createdAt;
  private Instant updatedAt;
  private int versionNumber;

  public GoalResponse() {}

  public GoalResponse(
          String id, String ownerId, String parentId, List<String> childrenIds,
          String title, String description, String dueDate,
          models.GoalStatus status, int latestPercentage,
          Instant createdAt, Instant updatedAt, int versionNumber) {
    this.id = id;
    this.ownerId = ownerId;
    this.parentId = parentId;
    this.childrenIds = childrenIds;
    this.title = title;
    this.description = description;
    this.dueDate = dueDate;
    this.status = status;
    this.latestPercentage = latestPercentage;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.versionNumber = versionNumber;
  }

  /** Convenience mapper from domain model. */
  public static GoalResponse from(models.Goal g) {
    return new GoalResponse(
            g.getId(), g.getOwnerId(), g.getParentId(), g.getChildrenId(),
            g.getTitle(), g.getDescription(), g.getDueDate(),
            g.getStatus(), g.getLatestPercentage(),
            g.getCreatedAt(), g.getUpdatedAt(), g.getVersionNumber());
  }

  public String getId() { return id; }
  public String getOwnerId() { return ownerId; }
  public String getParentId() { return parentId; }
  public List<String> getChildrenIds() { return childrenIds; }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public String getDueDate() { return dueDate; }
  public models.GoalStatus getStatus() { return status; }
  public int getLatestPercentage() { return latestPercentage; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public int getVersionNumber() { return versionNumber; }

  public void setId(String id) { this.id = id; }
  public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
  public void setParentId(String parentId) { this.parentId = parentId; }
  public void setChildrenIds(List<String> childrenIds) { this.childrenIds = childrenIds; }
  public void setTitle(String title) { this.title = title; }
  public void setDescription(String description) { this.description = description; }
  public void setDueDate(String dueDate) { this.dueDate = dueDate; }
  public void setStatus(models.GoalStatus status) { this.status = status; }
  public void setLatestPercentage(int latestPercentage) { this.latestPercentage = latestPercentage; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
  public void setVersionNumber(int versionNumber) { this.versionNumber = versionNumber; }
}

