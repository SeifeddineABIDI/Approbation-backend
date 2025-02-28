package tn.esprit.pfe.approbation.dtos;

public class TaskStatsDto {
    private long completedTasks;
    private long waitingTasks;
    private long authCredit;
    private long authOcc;

    public TaskStatsDto(long completedTasks, long waitingTasks, long authCredit, long authOcc) {
        this.completedTasks = completedTasks;
        this.waitingTasks = waitingTasks;
        this.authCredit = authCredit;
        this.authOcc = authOcc;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public long getWaitingTasks() {
        return waitingTasks;
    }
    public long getAuthCredit() {
        return authCredit;
    }
    public long getAuthOcc() {
        return authOcc;
    }
}