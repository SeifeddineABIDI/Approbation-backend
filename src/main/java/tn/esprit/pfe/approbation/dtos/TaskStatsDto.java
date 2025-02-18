package tn.esprit.pfe.approbation.dtos;

public class TaskStatsDto {
    private long completedTasks;
    private long waitingTasks;

    public TaskStatsDto(long completedTasks, long waitingTasks) {
        this.completedTasks = completedTasks;
        this.waitingTasks = waitingTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public long getWaitingTasks() {
        return waitingTasks;
    }
}