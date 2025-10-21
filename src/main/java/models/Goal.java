/*
ChatGPT 5 was used to add null/blank checks, range validation,
ISO date validation, defensive copies, and safer child ops.
It was also used to add comments.
 */

package models;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

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
  public Goal(String ownerId) {
    this.id = UUID.randomUUID().toString();
    this.ownerId = requireNonBlank(ownerId, "ownerId");
    this.childrenId = new ArrayList<>();
    this.goalStatus = Objects.requireNonNull(GoalStatus.ACTIVE, "status"); // or pick your enum default
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

  // --- Getters (children list exposed as unmodifiable view) ---

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

  // --- Setters (validate, then bump version/updatedAt) ---

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

  /** Replaces children with a sanitized copy (no nulls/blanks/dups). */
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

  /** Accepts null to unset. Must be ISO yyyy-MM-dd if non-null/non-blank. */
  public void setDueDate(String dueDate) {
    this.dueDate = validateOrNullIsoDate(dueDate);
    bumpVersion();
  }

  public void setStatus(GoalStatus goalStatus) {
    this.goalStatus = Objects.requireNonNull(goalStatus, "status");
    bumpVersion();
  }

  /** Clamped to [0,100]. */
  public void setLatestPercentage(int latestPercentage) {
    this.latestPercentage = clampPercentage(latestPercentage);
    bumpVersion();
  }

  /** updatedAt must not be before createdAt. */
  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = requireNotBefore(Objects.requireNonNull(updatedAt, "updatedAt"), this.createdAt, "updatedAt");
    // don't bump version here; this method *is* the timestamp change.
  }

  /** createdAt can only be set once safely; if you keep it mutable, guard it. */
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

  /** Adds childId if non-blank and not present. Returns true if added. */
  public boolean addChild(String childId) {
    String id = requireNonBlank(childId, "childId");
    if (childrenId.contains(id)) return false;
    childrenId.add(id);
    bumpVersion();
    return true;
  }

  /** Removes childId if present. Returns true if removed. */
  public boolean removeChild(String childId) {
    if (childId == null) return false;
    boolean removed = childrenId.remove(childId);
    if (removed) bumpVersion();
    return removed;
  }

  // --- Convenience ---

  /** Touches updatedAt to now and bumps version. */
  public void touchUpdatedAt() {
    bumpVersion();
  }

  // --- Equality/Hash/ToString by id (stable identity) ---

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Goal)) return false;
    Goal goal = (Goal) o;
    return Objects.equals(id, goal.id);
  }

  @Override public int hashCode() { return Objects.hash(id); }

  @Override public String toString() {
    return "Goal{id='" + id + "', title='" + title + "', status=" + goalStatus +
            ", latestPercentage=" + latestPercentage + ", dueDate=" + dueDate + "}";
  }

  // --- Private helpers ---

  private static String requireNonBlank(String s, String field) {
    if (s == null || s.trim().isEmpty())
      throw new IllegalArgumentException(field + " must be non-blank");
    return s;
  }

  private static String requireNonBlankOrGenerate(String s, String field) {
    return (s == null || s.trim().isEmpty()) ? UUID.randomUUID().toString() : s;
  }

  private static String blankToNull(String s) {
    return (s == null || s.trim().isEmpty()) ? null : s;
  }

  /** Accepts null/blank → null. Otherwise must parse as ISO yyyy-MM-dd. */
  private static String validateOrNullIsoDate(String s) {
    if (s == null || s.trim().isEmpty()) return null;
    try {
      LocalDate.parse(s.trim()); // ISO_LOCAL_DATE by default
      return s.trim();
    } catch (DateTimeParseException ex) {
      throw new IllegalArgumentException("dueDate must be ISO yyyy-MM-dd");
    }
  }

  private static int clampPercentage(int p) {
    if (p < 0) return 0;
    if (p > 100) return 100;
    return p;
  }

  private static Instant requireNotBefore(Instant candidate, Instant floor, String field) {
    if (candidate.isBefore(floor)) {
      throw new IllegalArgumentException(field + " cannot be before createdAt");
    }
    return candidate;
  }

  /** Return a new mutable list with null/blank removed and duplicates collapsed, preserving order. */
  private static List<String> sanitizeIdList(List<String> ids) {
    if (ids == null || ids.isEmpty()) return new ArrayList<>();
    LinkedHashSet<String> set = new LinkedHashSet<>();
    for (String id : ids) {
      if (id != null && !id.trim().isEmpty()) set.add(id.trim());
    }
    return new ArrayList<>(set);
  }

  /** Bumps version and updatedAt to now. */
  private void bumpVersion() {
    this.updatedAt = Instant.now();
    this.versionNumber = Math.max(1, this.versionNumber + 1);
  }

  /** Only updates updatedAt (no semantic change). */
  private void bumpTimestampsOnly() {
    this.updatedAt = Instant.now();
  }

}
