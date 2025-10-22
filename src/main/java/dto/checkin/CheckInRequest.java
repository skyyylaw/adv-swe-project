package dto.checkin;

import java.time.LocalDateTime;

import models.CheckIn;

public class CheckInRequest {
    private Long goalId;
    private LocalDateTime checkInDate;
    private String notes;

    public CheckInRequest() {
    }

    public CheckInRequest(Long goalId, LocalDateTime checkInDate, String notes) {
        this.goalId = goalId;
        this.checkInDate = checkInDate;
        this.notes = notes;
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

    public static CheckIn toEntity(CheckInRequest request) {
        if (request == null) {
            return null;
        }

        CheckIn checkIn = new CheckIn();
        checkIn.setGoalId(request.getGoalId());
        checkIn.setCheckInDate(request.getCheckInDate());
        checkIn.setNotes(request.getNotes());
        checkIn.setCreatedAt(LocalDateTime.now());
        checkIn.setUpdatedAt(LocalDateTime.now());
        checkIn.setVersion(1);

        return checkIn;
    }

}