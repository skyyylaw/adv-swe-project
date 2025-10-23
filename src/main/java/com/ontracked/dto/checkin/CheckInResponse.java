package com.ontracked.dto.checkin;


import com.ontracked.model.CheckIn;

import java.time.LocalDateTime;

public class CheckInResponse {
    private Long id;
    private Long goalId;
    private LocalDateTime checkInDate;
    private String notes;

    public CheckInResponse() {
    }

    public CheckInResponse(Long id, Long goalId, LocalDateTime checkInDate, String notes) {
        this.id = id;
        this.goalId = goalId;
        this.checkInDate = checkInDate;
        this.notes = notes;
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


    public static CheckInResponse toResponse(CheckIn checkIn) {
        if (checkIn == null) {
            return null;
        }

        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getGoalId(),
                checkIn.getCheckInDate(),
                checkIn.getNotes()
        );
    }

}
