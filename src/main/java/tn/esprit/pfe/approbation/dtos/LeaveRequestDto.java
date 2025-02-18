package tn.esprit.pfe.approbation.dtos;


import java.time.LocalDate;

public class LeaveRequestDto {
    private String userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean goAfterMidday=false;
    private Boolean backAfterMidday=false;

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
