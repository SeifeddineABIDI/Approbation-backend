package tn.esprit.pfe.approbation.dtos;


import java.time.LocalDate;

public class LeaveRequestDto {
    private String userId;
    private LocalDate startDate;
    private LocalDate endDate;

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
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
