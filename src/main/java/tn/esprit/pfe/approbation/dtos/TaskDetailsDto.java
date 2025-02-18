package tn.esprit.pfe.approbation.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class TaskDetailsDto {

    private String id;
    private String procInstId;
    private String name;
    private String assignee;
    private String owner;
    private Date startTime;
    private Date endTime;
    private String description;
    private String deleteReason;
    private Boolean leaveApproved;

    public TaskDetailsDto(String id,String procInstId ,String name, String assignee, String owner, Date startTime, Date endTime, String description, String deleteReason, Boolean leaveApproved) {
        this.id = id;
        this.procInstId = procInstId;
        this.name = name;
        this.assignee = assignee;
        this.owner = owner;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.deleteReason = deleteReason;
        this.leaveApproved = leaveApproved;
    }


}
