package tn.esprit.pfe.approbation.DTOs;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskConfirmationDTO {

    private String approvalStatus;
    private String comments;

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

}