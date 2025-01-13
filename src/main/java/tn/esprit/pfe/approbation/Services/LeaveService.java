package tn.esprit.pfe.approbation.Services;

import jakarta.transaction.Transactional;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.pfe.approbation.DTOs.LeaveRequestDto;
import tn.esprit.pfe.approbation.Entities.LeaveRequest;
import tn.esprit.pfe.approbation.Entities.User;
import tn.esprit.pfe.approbation.Repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.Repositories.UserRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class LeaveService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    public String handleLeaveRequest(LeaveRequestDto request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() ->
                new IllegalArgumentException("User not found"));

        User manager = user.getManager();
        if (manager == null) {
            return "User does not have a manager assigned.";
        }
        long daysRequested = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (user.getSoldeConge() >= daysRequested) {
            LeaveRequest leaveRequest = new LeaveRequest();
            leaveRequest.setUser(user);
            leaveRequest.setStartDate(request.getStartDate());
            leaveRequest.setEndDate(request.getEndDate());
            leaveRequest.setManagerApproved(false);
            leaveRequest.setManagerComments(null);
            leaveRequest.setManagerApprovalDate(null);
            leaveRequest.setRhApproved(false);
            leaveRequest.setRhComments(null);
            leaveRequest.setRhApprovalDate(null);
            leaveRequestRepository.save(leaveRequest);

            Map<String, Object> variables = new HashMap<>();
            variables.put("userId", request.getUserId());
            variables.put("startDate", request.getStartDate());
            variables.put("endDate", request.getEndDate());
            variables.put("daysRequested", daysRequested);
            variables.put("managerId", manager.getId());
            variables.put("leaveRequestId", leaveRequest    .getId());

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_0d8lhc1", variables);

            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            if (task != null) {
                taskService.setAssignee(task.getId(), manager.getId());
            } else {
                throw new IllegalStateException("Task not found for process instance: " + processInstance.getId());
            }

            return "Leave request created and process started with process instance id: " + processInstance.getId();
        } else {
            return "Insufficient leave balance.";
        }
    }

    public void confirmManagerTask(String taskId, String approvalStatus, String comments) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new IllegalStateException("Task not found for taskId: " + taskId);
        }
        VariableMap variables = Variables.createVariables();
        boolean leaveApproved = "approved".equalsIgnoreCase(approvalStatus);
        variables.put("leaveApproved", leaveApproved);
        variables.put("managerComments", comments);
        taskService.complete(task.getId(), variables);
    }

    public void confirmRhTask(String taskId, String approvalStatus, String comments) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new IllegalStateException("Task not found for taskId: " + taskId);
        }

        VariableMap variables = Variables.createVariables();

        // Map the approvalStatus to leaveApproved (for RH approval)
        boolean leaveApproved = "approved".equalsIgnoreCase(approvalStatus);  // true for "approved", false for "rejected"
        variables.put("leaveApproved", leaveApproved);

        // Pass the RH comments
        variables.put("rhComments", comments);

        // Complete the task
        taskService.complete(task.getId(), variables);
    }

    public void confirmManagerTask1(UUID taskId, String approvalStatus, String comments) {
        Task task = taskService.createTaskQuery().taskId(taskId.toString()).singleResult();
        if (task == null) {
            throw new IllegalStateException("Task not found for taskId: " + taskId);
        }
        Long leaveRequestId = (Long) taskService.getVariable(task.getId(), "leaveRequestId");
        if (leaveRequestId == null) {
            throw new IllegalStateException("leaveRequestId variable not found in task process");
        }
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() ->
                new IllegalStateException("LeaveRequest not found"));
        boolean leaveApproved = "approved".equalsIgnoreCase(approvalStatus);
        leaveRequest.updateManagerApproval(leaveApproved, comments);
        leaveRequestRepository.save(leaveRequest);
        VariableMap variables = Variables.createVariables();
        variables.put("leaveApproved", leaveApproved);
        variables.put("managerComments", comments);
        taskService.complete(task.getId(), variables);
    }

    public void confirmRhTask1(UUID taskId, String approvalStatus, String comments) {
        Task task = taskService.createTaskQuery().taskId(taskId.toString()).singleResult();
        if (task == null) {
            throw new IllegalStateException("Task not found for taskId: " + taskId);
        }
        Long leaveRequestId = (Long) taskService.getVariable(task.getId(), "leaveRequestId");
        if (leaveRequestId == null) {
            throw new IllegalStateException("leaveRequestId variable not found in task process");
        }
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId).orElseThrow(() ->
                new IllegalStateException("LeaveRequest not found"));

        boolean leaveApproved = "approved".equalsIgnoreCase(approvalStatus);
        leaveRequest.updateRhApproval(leaveApproved, comments);
        leaveRequestRepository.save(leaveRequest);
        VariableMap variables = Variables.createVariables();
        variables.put("leaveApproved", leaveApproved);
        variables.put("rhComments", comments);
        taskService.complete(task.getId(), variables);
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void accrueLeaveForWorkingUsers() {
        double dailyAccrualRate = 20.0 / 12;
        List<User> users = userRepository.findAll();
        for (User user : users) {
                user.setSoldeConge(user.getSoldeConge() + dailyAccrualRate);
                userRepository.save(user);
        }
    }

    @Scheduled(cron = "0 5 0 * * ?")
    public void updateLeaveStatus() {
        LocalDate today = LocalDate.now();
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findApprovedLeaveRequestsEndingToday(today);
        for (LeaveRequest leaveRequest : leaveRequests) {
            User user = leaveRequest.getUser();
            if (user != null) {
                user.setOnLeave(false);
                userRepository.save(user);
            }
        }
    }
}