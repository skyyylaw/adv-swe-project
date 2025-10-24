/*
ChatGPT 5 was used to add null/blank checks, range validation,
ISO date validation, defensive copies, and safer child ops.
It was also used to add comments and enforce brace consistency.
 */

package com.ontracked.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain model for a Goal with basic validation and defensive programming.
 * - IDs and ownerId must be non-blank.
 * - status must be non-null.
 * - latestPercentage is clamped to [0, 100].
 * - dueDate must be ISO-8601 (yyyy-MM-dd) if provided.
 * - childrenId is kept duplicate- and null-free via defensive copies.
 * - setters validate inputs and bump updatedAt/versionNumber.
 */
public final class Goal {

  private String id;
  private String ownerId;
  private String parentId;                  // nullable
  private List<String> childrenId;          // never null; mutable list guarded by methods
  private String title;                     // nullable
  private String description;               // nullable
  private String dueDate;                   // nullable; ISO yyyy-MM-dd
  private GoalStatus goalStatus;
  private int latestPercentage;             // 0..100
  private Instant createdAt;
  private Instant updatedAt;
  private int versionNumber;                // >= 1

  // --- Constructors ---

  /** Minimal constructor; sets sane defaults. */
  @JsonCreator
  public Goal(@JsonProperty("ownerId") String ownerId) {
    this.id = UUID.randomUUID().toString();
    this.ownerId = requireNonBlank(ownerId, "ownerId");
    this.childrenId = new ArrayList<>();
    this.goalStatus = Objects.requireNonNull(GoalStatus.ACTIVE, "status");
    this.latestPercentage = 0;
    this.createdAt = Instant.now();
    this.updatedAt = this.createdAt;
    this.versionNumber = 1;
  }

  /** Full constructor with validation and defensive copies. */
  public Goal(
          String id,
          String ownerId,
          String parentId,
          List<String> childrenId,
          String title,
          String description,
          String dueDate,
          GoalStatus goalStatus,
          int latestPercentage,
          Instant createdAt,
          Instant updatedAt,
          int versionNumber) {

    this.id = requireNonBlankOrGenerate(id, "id");
    this.ownerId = requireNonBlank(ownerId, "ownerId");
    this.parentId = blankToNull(parentId);
    this.childrenId = sanitizeIdList(childrenId);
    this.title = blankToNull(title);
    this.description = blankToNull(description);
    this.dueDate = validateOrNullIsoDate(dueDate);
    this.goalStatus = Objects.requireNonNull(goalStatus, "status");
    this.latestPercentage = clampPercentage(latestPercentage);
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.updatedAt = requireNotBefore(Objects.requireNonNull(updatedAt, "updatedAt"), this.createdAt, "updatedAt");
    this.versionNumber = Math.max(1, versionNumber);
  }

  // --- Getters ---

  public String getId() { return id; }
  public String getOwnerId() { return ownerId; }
  public String getParentId() { return parentId; }
  public List<String> getChildrenId() { return Collections.unmodifiableList(childrenId); }
  public String getTitle() { return title; }
  public String getDescription() { return description; }
  public String getDueDate() { return dueDate; }
  public GoalStatus getStatus() { return goalStatus; }
  public int getLatestPercentage() { return latestPercentage; }
  public Instant getCreatedAt() { return createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public int getVersionNumber() { return versionNumber; }

  // --- Setters ---

  public void setId(String id) {
    this.id = requireNonBlank(id, "id");
    bumpVersion();
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = requireNonBlank(ownerId, "ownerId");
    bumpVersion();
  }

  public void setParentId(String parentId) {
    this.parentId = blankToNull(parentId);
    bumpVersion();
  }

  public void setChildrenId(List<String> childrenId) {
    this.childrenId = sanitizeIdList(childrenId);
    bumpVersion();
  }

  public void setTitle(String title) {
    this.title = blankToNull(title);
    bumpVersion();
  }

  public void setDescription(String description) {
    this.description = blankToNull(description);
    bumpVersion();
  }

  public void setDueDate(String dueDate) {
    this.dueDate = validateOrNullIsoDate(dueDate);
    bumpVersion();
  }

  public void setStatus(GoalStatus goalStatus) {
    this.goalStatus = Objects.requireNonNull(goalStatus, "status");
    bumpVersion();
  }

  public void setLatestPercentage(int latestPercentage) {
    this.latestPercentage = clampPercentage(latestPercentage);
    bumpVersion();
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = requireNotBefore(Objects.requireNonNull(updatedAt, "updatedAt"), this.createdAt, "updatedAt");
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    if (this.updatedAt != null) {
      this.updatedAt = requireNotBefore(this.updatedAt, this.createdAt, "updatedAt");
    }
    bumpVersion();
  }

  public void setVersionNumber(int versionNumber) {
    this.versionNumber = Math.max(1, versionNumber);
    bumpTimestampsOnly();
  }

  // --- Children helpers ---

  public boolean addChild(String childId) {
    String id = requireNonBlank(childId, "childId");
    if (childrenId.contains(id)) {
      return false;
    }
    childrenId.add(id);
    bumpVersion();
    return true;
  }

  public boolean removeChild(String childId) {
    if (childId == null) {
      return false;
    }
    boolean removed = childrenId.remove(childId);
    if (removed) {
      bumpVersion();
    }
    return removed;
  }

  // --- Convenience ---

  public void touchUpdatedAt() {
    bumpVersion();
  }

  // --- Equality / Hash / ToString ---

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Goal)) {
      return false;
    }
    Goal goal = (Goal) o;
    return Objects.equals(id, goal.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Goal{id='" + id + "', title='" + title + "', status=" + goalStatus +
            ", latestPercentage=" + latestPercentage + ", dueDate=" + dueDate + "}";
  }

  // --- Private helpers ---

  private static String requireNonBlank(String s, String field) {
    if (s == null || s.trim().isEmpty()) {
      throw new IllegalArgumentException(field + " must be non-blank");
    }
    return s;
  }

  private static String requireNonBlankOrGenerate(String s, String field) {
    if (s == null || s.trim().isEmpty()) {
      return UUID.randomUUID().toString();
    }
    return s;
  }

  private static String blankToNull(String s) {
    if (s == null || s.trim().isEmpty()) {
      return null;
    }
    return s;
  }

  private static String validateOrNullIsoDate(String s) {
    if (s == null || s.trim().isEmpty()) {
      return null;
    }
    try {
      LocalDate.parse(s.trim());
      return s.trim();
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("dueDate must be ISO yyyy-MM-dd");
    }
  }

  private static int clampPercentage(int p) {
    if (p < 0) {
      return 0;
    }
    if (p > 100) {
      return 100;
    }
    return p;
  }

  private static Instant requireNotBefore(Instant candidate, Instant floor, String field) {
    if (candidate.isBefore(floor)) {
      throw new IllegalArgumentException(field + " cannot be before createdAt");
    }
    return candidate;
  }

  private static List<String> sanitizeIdList(List<String> ids) {
    if (ids == null || ids.isEmpty()) {
      return new ArrayList<>();
    }
    LinkedHashSet<String> set = new LinkedHashSet<>();
    for (String id : ids) {
      if (id != null && !id.trim().isEmpty()) {
        set.add(id.trim());
      }
    }
    return new ArrayList<>(set);
  }

  private void bumpVersion() {
    this.updatedAt = Instant.now();
    this.versionNumber = Math.max(1, this.versionNumber + 1);
  }

  private void bumpTimestampsOnly() {
    this.updatedAt = Instant.now();
  }
}
