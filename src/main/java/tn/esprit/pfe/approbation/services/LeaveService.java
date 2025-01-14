package tn.esprit.pfe.approbation.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
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
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;

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
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private JavaMailSenderImpl mailSender;

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
            variables.put("managerId", manager.getMatricule());
            variables.put("leaveRequestId", leaveRequest.getId());

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Process_0d8lhc1", variables);

            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            if (task != null) {
                taskService.setAssignee(task.getId(), manager.getMatricule());
                try {
                    sendRequestSumbmittedEmail(
                            manager.getEmail(), // To (recipient)
                            "New Leave Request Submitted",
                            "submitted", // Email status
                            "A leave request has been submitted for your review.",
                            user.getFirstName() + " " + user.getLastName(), // User's name
                            manager.getFirstName() + " " + manager.getLastName(), // Manager's name
                            request.getStartDate().toString(), // Start date of leave
                            request.getEndDate().toString() // End date of leave
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
        User user = leaveRequest.getUser();
        User manager = user.getManager();
        System.out.println("leaveApproved: " + leaveApproved + "");
        if (!leaveApproved) {
            sendEmailNotification(leaveApproved, comments, leaveRequest.getUser().getEmail(), user.getFirstName() + " " + user.getLastName());
        }
        if (leaveApproved){
            try {
                sendRequestSumbmittedEmail(
                        "seifeddine.abidi@esprit.tn", // To (recipient)
                        "New Leave Request Submitted",
                        "submitted", // Email status
                        "A leave request has been submitted for your review.",
                        user.getFirstName() + " " + user.getLastName(), // User's name
                        manager.getFirstName() + " " + manager.getLastName(), // Manager's name
                        leaveRequest.getStartDate().toString(), // Start date of leave
                        leaveRequest.getEndDate().toString() // End date of leave
                );
            } catch (MessagingException e) {
                throw new IllegalStateException("Failed to send email notification", e);
            }
        }
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
        User user = leaveRequest.getUser();
        sendEmailNotification(leaveApproved, comments, leaveRequest.getUser().getEmail(),user.getFirstName() + " " + user.getLastName());
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
    private void sendEmailNotification(boolean leaveApproved, String comments, String userEmail, String userName) {
        try {
            if (leaveApproved) {
                // Send approval email
                sendEmail(userEmail, "Your leave request has been approved.", "approved", "",userName);
            } else {
                // Send rejection email with comments
                sendEmail(userEmail, "Your leave request has been rejected.", "rejected", "Reason: " + comments,userName);
            }
        } catch (Exception e) {
        }
    }
    public void sendEmail(String to, String subject, String status, String message,String userName) throws MessagingException {
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
                                           String userName, String managerName, String startDate, String endDate)  throws MessagingException {
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
        return leaveRequestRepository.findByUserMatriculeAndManagerApprovedTrueAndRhApprovedTrueOrderByIdDesc(userId);
    }
}