package tn.esprit.pfe.approbation.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequestDto {
    private String userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean goAfterMidday = false;
    private Boolean backAfterMidday = false;

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    // Add this method to convert LocalDate to LocalDateTime
    public LocalDateTime getStartDateTime() {
        return startDate != null ? startDate.atStartOfDay() : null; // Default to 00:00:00
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    // Add this method to convert LocalDate to LocalDateTime
    public LocalDateTime getEndDateTime() {
        return endDate != null ? endDate.atStartOfDay() : null; // Default to 00:00:00
    }
    public boolean isGoAfterMidday() {
        return goAfterMidday;
    }
    public void setGoAfterMidday(boolean goAfterMidday) {
        this.goAfterMidday = goAfterMidday;
    }
    public boolean isBackAfterMidday() {
        return backAfterMidday;
    }
    public void setBackAfterMidday(boolean backAfterMidday) {
        this.backAfterMidday = backAfterMidday;
    }
}