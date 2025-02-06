package tn.esprit.pfe.approbation.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskDTO {

    private String taskId;
    private String taskName;
    private String processInstanceId;
    private String assignee;
    private String requester;
    private LocalDate startDate;
    private LocalDate endDate;

    public TaskDTO(String taskId, String taskName, String processInstanceId, String assignee,
                   String requester, LocalDate startDate, LocalDate endDate) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.processInstanceId = processInstanceId;
        this.assignee = assignee;
        this.requester = requester;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {}

    public String getTaskName() {
        return taskName;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProcessInstanceId() {
        return processInstanceId;}
    public void setProcessInstanceId(String processInstanceId) {}

    public String getAssignee() {
        return assignee;
    }
    public void setAssignee(String assignee) {}
    public String getRequester() {
        return requester;
    }
    public void setRequester(String requester) {}
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {}
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {}
    public String toString() {
        return taskId + "\t" + taskName + "\t" + processInstanceId + "\t" + assignee;
    }


}

