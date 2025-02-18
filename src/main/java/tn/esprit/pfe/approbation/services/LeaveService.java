package tn.esprit.pfe.approbation.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tn.esprit.pfe.approbation.dtos.LeaveRequestDto;
import tn.esprit.pfe.approbation.dtos.TaskDetailsDto;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private JavaMailSenderImpl mailSender;
    @Autowired
    private HistoryService historyService;

    public String handleLeaveRequest(LeaveRequestDto request) {
        User user = userRepository.findByMatricule(request.getUserId());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        User manager = user.getManager();
        if (manager == null) {
            return "User does not have a manager assigned.";
        }
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findOverlappingLeaveRequests(
                request.getUserId(),
                request.getStartDate(),
                request.getEndDate()
        );
        if (!overlappingRequests.isEmpty()) {
            return "There is already a leave request that overlaps with this period.";
        }
        boolean goAfterMidday = request.isGoAfterMidday();
        boolean backAfterMidday = request.isBackAfterMidday();
        long daysRequested = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (user.getSoldeConge() >= daysRequested) {
            LeaveRequest leaveRequest = new LeaveRequest();
            leaveRequest.setUser(user);
            leaveRequest.setStartDate(request.getStartDate());
            leaveRequest.setEndDate(request.getEndDate());
            leaveRequest.setGoAfterMidday(goAfterMidday);
            leaveRequest.setBackAfterMidday(backAfterMidday);
            Map<String, Object> variables = new HashMap<>();
            variables.put("userId", request.getUserId());
            variables.put("startDate", request.getStartDate());
            variables.put("endDate", request.getEndDate());
            variables.put("goAfterMidday", request.isGoAfterMidday());
            variables.put("backAfterMidday", request.isBackAfterMidday());
            variables.put("daysRequested", daysRequested);
            variables.put("managerId", manager.getMatricule());
            variables.put("leaveRequestId", UUID.randomUUID().toString());

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_0d8lhc1", variables);
            leaveRequest.setProcInstId(processInstance.getProcessInstanceId());
            leaveRequestRepository.save(leaveRequest);
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
            for (Task tas : tasks) {
                taskService.setOwner(tas.getId(), request.getUserId());
            }
            if (!tasks.isEmpty()) {
                Task firstTask = tasks.get(0);
                taskService.setAssignee(firstTask.getId(), manager.getMatricule());
                try {
                    sendRequestSumbmittedEmail(
                            manager.getEmail(),
                            "New Leave Request Submitted",
                            "submitted",
                            "A leave request has been submitted for your review.",
                            user.getFirstName() + " " + user.getLastName(),
                            manager.getFirstName() + " " + manager.getLastName(),
                            request.getStartDate().toString(),
                            request.getEndDate().toString()
                    );
                } catch (MessagingException e) {
                    throw new IllegalStateException("Failed to send email notification", e);
                }
            } else {
                throw new IllegalStateException("Task not found for process instance: " + processInstance.getId());
            }
            return "Leave request created and process started with process instance id: " + processInstance.getId();
        } else {
            return "Insufficient leave balance.";
        }
    }

    public void confirmManagerTask1(UUID taskId, String approvalStatus, String comments) {
        Task task = taskService.createTaskQuery().taskId(taskId.toString()).singleResult();
        if (task == null) {
            throw new IllegalStateException("Task not found for taskId: " + taskId);
        }

        boolean leaveApproved = "approved".equalsIgnoreCase(approvalStatus);
        LocalDate startDate = (LocalDate) taskService.getVariable(task.getId(), "startDate");
        LocalDate endDate = (LocalDate) taskService.getVariable(task.getId(), "endDate");
        String leaveRequestId = taskService.getVariable(task.getId(), "leaveRequestId").toString();
        if (leaveRequestId == null) {
            throw new IllegalStateException("leaveRequestId variable not found in task process");
        }
        taskService.setVariable(task.getId(), "managerApproved", leaveApproved);
        taskService.setVariable(task.getId(), "managerComments", comments);
        taskService.setDescription(task.getId(), comments);
        VariableMap variables = Variables.createVariables();
        variables.put("leaveApproved", leaveApproved);
        variables.put("managerComments", comments);
        taskService.complete(task.getId(), variables);
        User user = userRepository.findByMatricule(task.getOwner());
        User manager = user.getManager();
        System.out.println("leaveApproved: " + leaveApproved + "");
        if (!leaveApproved) {
            sendEmailNotification(leaveApproved, comments, user.getEmail(), user.getFirstName() + " " + user.getLastName());
            LeaveRequest leaveRequest = leaveRequestRepository.findByProcInstId(task.getProcessInstanceId());
            if (leaveRequest != null) {
                leaveRequest.setApproved(false);  // Set approved to false
                leaveRequestRepository.save(leaveRequest);  // Save the updated LeaveRequest
            } else {
                throw new IllegalStateException("LeaveRequest not found for process instance: " + task.getProcessInstanceId());
            }
        }
        if (leaveApproved) {
            try {
                sendRequestSumbmittedEmail(
                        "seifeddine.abidi@esprit.tn",
                        "New Leave Request Submitted",
                        "submitted",
                        "A leave request has been submitted for your review.",
                        user.getFirstName() + " " + user.getLastName(),
                        manager.getFirstName() + " " + manager.getLastName(),
                        startDate.toString(),
                        endDate.toString()
                );
            } catch (MessagingException e) {
                throw new IllegalStateException("Failed to send email notification", e);
            }
            List<Task> rhTasks = taskService.createTaskQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .taskDefinitionKey("Activity_0mgsywo")  // Correct key for RH task
                    .list();
            if (!rhTasks.isEmpty()) {
                Task rhTask = rhTasks.get(0);
                // Set the owner for the RH task (e.g., HR User ID)
                taskService.setOwner(rhTask.getId(), task.getOwner());  // Set to the RH user ID
            }
        }
    }

    public void confirmRhTask1(UUID taskId, String approvalStatus, String comments,String matricule) {
        Task task = taskService.createTaskQuery().taskId(taskId.toString()).singleResult();
        if (task == null) {
            throw new IllegalStateException("Task not found for taskId: " + taskId);
        }
        LeaveRequest leaveRequest = leaveRequestRepository.findByProcInstId(task.getProcessInstanceId());
        String leaveRequestId = taskService.getVariable(task.getId(), "leaveRequestId").toString();
        if (leaveRequestId == null) {
            throw new IllegalStateException("leaveRequestId variable not found in task process");
        }
        System.out.println("leaveRequestId: " + leaveRequestId + "");
        boolean leaveApproved = "approved".equalsIgnoreCase(approvalStatus);
        leaveRequest.setApproved(leaveApproved);
        leaveRequestRepository.save(leaveRequest);
        taskService.setAssignee(task.getId(),matricule);
        taskService.setDescription(task.getId(), comments);
        VariableMap variables = Variables.createVariables();
        variables.put("leaveApproved", leaveApproved);
        variables.put("rhComments", comments);
        taskService.complete(task.getId(), variables);
        User user = userRepository.findByMatricule(task.getOwner());
        sendEmailNotification(leaveApproved, comments, user.getEmail(), user.getFirstName() + " " + user.getLastName());

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

    /*@Scheduled(cron = "0 5 0 * * ?")
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
    }*/

    private void sendEmailNotification(boolean leaveApproved, String comments, String userEmail, String userName) {
        try {
            if (leaveApproved) {
                sendEmail(userEmail, "Your leave request has been approved.", "approved", "", userName);
            } else {
                sendEmail(userEmail, "Your leave request has been rejected.", "rejected", "Reason: " + comments, userName);
            }
        } catch (Exception e) {
        }
    }

    public void sendEmail(String to, String subject, String status, String message, String userName) throws MessagingException {
        Context context = new Context();
        context.setVariable("status", status);
        context.setVariable("message", message);
        context.setVariable("userName", userName);
        String htmlContent = templateEngine.process("leaveRequestNotification", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        Resource logoResource = new ClassPathResource("templates/siga.png");
        InputStreamSource logoSource = logoResource::getInputStream;
        helper.addInline("logo", logoSource, "image/png");
        mailSender.send(mimeMessage);
    }

    public void sendRequestSumbmittedEmail(String to, String subject, String status, String message,
                                           String userName, String managerName, String startDate, String endDate) throws MessagingException {
        Context context = new Context();
        context.setVariable("status", status);
        context.setVariable("message", message);
        context.setVariable("userName", userName);
        context.setVariable("managerName", managerName);
        context.setVariable("startDate", startDate);
        context.setVariable("endDate", endDate);
        String htmlContent = templateEngine.process("requestSubmission", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        Resource logoResource = new ClassPathResource("templates/siga.png");
        InputStreamSource logoSource = logoResource::getInputStream;
        helper.addInline("logo", logoSource, "image/png");
        mailSender.send(mimeMessage);
    }

    public List<LeaveRequest> getApprovedLeaveRequests(String userId) {
        return leaveRequestRepository.findByUserMatriculeOrderByIdDesc(userId);
    }

    public List<TaskDetailsDto> getProcessTasksByInstanceId(String instanceId) {
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(instanceId)
                .list();
        return tasks.stream()
                .map(task -> new TaskDetailsDto(
                        task.getId(),
                        task.getRootProcessInstanceId(),
                        task.getName(),
                        task.getAssignee(),
                        task.getOwner(),
                        task.getStartTime(),
                        task.getEndTime(),
                        task.getDescription(),
                        task.getDeleteReason(),
                        null
                ))
                .collect(Collectors.toList());
    }
    public List<TaskDetailsDto> getTasksByAssignee(String assignee) {
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(assignee)
                .finished()
                .list();

        tasks.sort(Comparator.comparing(HistoricTaskInstance::getStartTime).reversed());

        return tasks.stream()
                .map(task -> {
                    String processInstanceId = task.getProcessInstanceId();
                    boolean processInstanceExists = runtimeService.createExecutionQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult() != null;
                    Boolean leaveApproved = false;
                    if (processInstanceExists) {
                        Map<String, Object> processVariables = runtimeService.getVariables(processInstanceId);
                        leaveApproved = (Boolean) processVariables.get("leaveApproved");
                        if (leaveApproved == null) {
                            leaveApproved = false;
                        }
                    }
                    return new TaskDetailsDto(
                            task.getId(),
                            task.getProcessInstanceId(),
                            task.getName(),
                            task.getAssignee(),
                            task.getOwner(),
                            task.getStartTime(),
                            task.getEndTime(),
                            task.getDescription(),
                            task.getDeleteReason(),
                            leaveApproved
                    );
                })
                .collect(Collectors.toList());
    }




}
