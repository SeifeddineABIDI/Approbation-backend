package tn.esprit.pfe.approbation.controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pfe.approbation.dtos.*;
import tn.esprit.pfe.approbation.delegate.UpdateConge;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import tn.esprit.pfe.approbation.services.LeaveService;
import tn.esprit.pfe.approbation.services.ReportService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private TaskService taskService;
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private UpdateConge updateConge;
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ReportService reportService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasksByUser(@PathVariable String userId) {
        try {
            TaskQuery taskQuery = taskService.createTaskQuery().taskAssignee(userId);
            List<Task> tasks = taskQuery.list();
            List<TaskDTO> taskDTOs = tasks.stream()
                    .map(task -> {
                        Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
                        String requester = (String) variables.get("userId");
                        Object startDateObj = variables.get("startDate");
                        Object endDateObj = variables.get("endDate");
                        System.out.println("startDate type: " + startDateObj.getClass().getName());
                        System.out.println("endDate type: " + endDateObj.getClass().getName());
                        LocalDateTime startDate = (startDateObj instanceof LocalDate)
                                ? ((LocalDate) startDateObj).atStartOfDay()
                                : (LocalDateTime) startDateObj;
                        LocalDateTime endDate = (endDateObj instanceof LocalDate)
                                ? ((LocalDate) endDateObj).atStartOfDay()
                                : (LocalDateTime) endDateObj;
                        return new TaskDTO(
                                task.getId(),
                                task.getName(),
                                task.getProcessInstanceId(),
                                task.getAssignee(),
                                requester,
                                startDate,
                                endDate,
                                task.getCreateTime()
                        );
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(taskDTOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/confirm/manager/{taskId}")
    public ResponseEntity<String> confirmManagerTask1(@PathVariable("taskId") UUID taskId, @Valid  @RequestBody TaskConfirmationDTO confirmationDto) {
        String approvalStatus = confirmationDto.getApprovalStatus();
        String comment = confirmationDto.getComments();
        try {
            leaveService.confirmManagerTask1(taskId, approvalStatus, comment);
            return ResponseEntity.ok("Manager's response saved and task completed successfully.");
        } catch (Exception e) {
            logger.error("Error completing manager task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error completing task: " + e.getMessage());
        }
    }

    @PostMapping("/confirm/rh/{taskId}/{matricule}")
    public ResponseEntity<String> confirmRhTask1(@PathVariable UUID taskId,@PathVariable String matricule, @Valid @RequestBody TaskConfirmationDTO confirmationDto) {
        String approvalStatus = confirmationDto.getApprovalStatus();
        String comment = confirmationDto.getComments();
        try {
            leaveService.confirmRhTask1(taskId, approvalStatus, comment,matricule);
            return ResponseEntity.ok("RH's response saved and task completed successfully.");
        } catch (Exception e) {
            logger.error("Error completing RH task", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error completing task: " + e.getMessage());
        }
    }

    @GetMapping("/getUserByMat/{matricule}")
    public UserDto getUserByMatricule(@PathVariable String matricule) {
        try {
            User user = userRepository.findByMatricule(matricule);
            if (user != null) {
                return UserDto.fromEntity(user);
            } else {
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/user/{userId}/task-stats")
    public TaskStatsDto getUserTaskStats(@PathVariable String userId) {
        User user = userRepository.findByMatricule(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        List<Task> assignedTasks = taskService.createTaskQuery().taskAssignee(userId).list();
        List<HistoricTaskInstance> completedTasksHistory = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(userId)
                .finished()
                .list();
        long completedTasks = completedTasksHistory.size();
        long waitingTasks = assignedTasks.size();
        return new TaskStatsDto(completedTasks, waitingTasks,user.getSoldeAutorisation(),user.getOccurAutorisation());
    }

    @GetMapping("/process/{instanceId}")
    public List<TaskDetailsDto> getTasksByProcessInstance(@PathVariable String instanceId) {
        return leaveService.getProcessTasksByInstanceId(instanceId);
    }

    @GetMapping("/get/assignee/{assignee}")
    public ResponseEntity<List<TaskDetailsDto>> getTasksByAssignee(@PathVariable String assignee) {
        List<TaskDetailsDto> tasks = leaveService.getTasksByAssignee(assignee);
        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/requests/{matricule}")
    public List<LeaveRequest> getLeaveRequestsByMatricule(@PathVariable String matricule) {
        return leaveRequestRepository.findByUserMatriculeOrderByIdDesc(matricule);
    }

    @GetMapping("/requestsConfirmed/{matricule}")
    public List<LeaveRequest> getLeaveRequestsConfirmedByMatricule(@PathVariable String matricule) {
        return leaveRequestRepository.findByUserMatriculeAndApprovedOrderByIdDesc(matricule,true);
    }

    @GetMapping("/generateAvisCongeReport")
    public void generateAvisCongeReport(@RequestParam String instanceId, HttpServletResponse response)throws JRException, IOException {
            byte[] reportBytes = reportService.generateAvisCongeReport(instanceId);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=AvisCongeReport.pdf");
            response.getOutputStream().write(reportBytes);
            response.getOutputStream().flush();
    }
}