package dto;

import jakarta.validation.constraints.*; // if you use validation; otherwise remove
import java.util.List;
import java.util.Objects;

/** Request payload to create a Goal. */
public class CreateGoalRequest {
  @NotBlank private String ownerId;              // required
  private String parentId;                       // optional
  @NotBlank @Size(max = 200) private String title;
  @Size(max = 2000) private String description;  // optional
  /** ISO-8601 yyyy-MM-dd; optional. Validate in service if not using Bean Validation. */
  private String dueDate;

  public CreateGoalRequest() {}

  public CreateGoalRequest(String ownerId, String parentId, String title, String description, String dueDate) {
    this.ownerId = ownerId;
    this.parentId = parentId;
    this.title = title;
    this.description = description;
    this.dueDate = dueDate;
  }

  public String getOwnerId() { return ownerId; }
  public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
  public String getParentId() { return parentId; }
  public void setParentId(String parentId) { this.parentId = parentId; }
  public String getTitle() { return title; }
  public void setTitle(String title) { this.title = title; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  public String getDueDate() { return dueDate; }
  public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}
