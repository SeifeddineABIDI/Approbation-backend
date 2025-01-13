package tn.esprit.pfe.approbation.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LEAVE_REQUEST")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate requestDate = LocalDate.now();

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = true)
    private Boolean managerApproved;

    @Column(nullable = true)
    private String managerComments;

    @Column(nullable = true)
    private LocalDateTime managerApprovalDate;

    @Column(nullable = true)
    private Boolean rhApproved;

    @Column(nullable = true)
    private String rhComments;

    @Column(nullable = true)
    private LocalDateTime rhApprovalDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Getters and Setters for user
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }
    public LocalDate getRequestDate() {
        return requestDate;
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
    public void setRequestDate(LocalDate requestDate) {}
    public Boolean getManagerApproved() {
        return managerApproved;
    }
    public void setManagerApproved(Boolean managerApproved) {}
    public String getManagerComments() {
        return managerComments;
    }
    public void setManagerComments(String managerComments) {}
    public LocalDateTime getManagerApprovalDate() {
        return managerApprovalDate;
    }
    public void setManagerApprovalDate(LocalDateTime managerApprovalDate) {}
    public Boolean getRhApproved() {
        return rhApproved;
    }
    public void setRhApproved(Boolean rhApproved) {}
    public String getRhComments() {
        return rhComments;
    }
    public void setRhComments(String rhComments) {}
    public LocalDateTime getRhApprovalDate() {
        return rhApprovalDate;
    }
    public void setRhApprovalDate(LocalDateTime rhApprovalDate) {}

    public void updateManagerApproval(Boolean approved, String comments) {
        this.managerApproved = approved;
        this.managerComments = comments;
        this.managerApprovalDate = LocalDateTime.now();
    }

    public void updateRhApproval(Boolean approved, String comments) {
        this.rhApproved = approved;
        this.rhComments = comments;
        this.rhApprovalDate = LocalDateTime.now();
    }
}


