package tn.esprit.pfe.approbation.controllers;

import jakarta.validation.Valid;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pfe.approbation.dtos.TaskConfirmationDTO;
import tn.esprit.pfe.approbation.dtos.TaskDTO;
import tn.esprit.pfe.approbation.delegate.UpdateConge;
import tn.esprit.pfe.approbation.dtos.UserDto;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import tn.esprit.pfe.approbation.services.LeaveService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/tasks")
public class TaskController {

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasksByUser(@PathVariable String userId) {
        try {
            TaskQuery taskQuery = taskService.createTaskQuery().taskAssignee(userId);
            List<Task> tasks = taskQuery.list();

            List<TaskDTO> taskDTOs = tasks.stream()
                    .map(task -> {
                        Map<String, Object> variables = runtimeService.getVariables(task.getProcessInstanceId());
                        String requester = (String) variables.get("userId");
                        LocalDate startDate = (LocalDate) variables.get("startDate");
                        LocalDate endDate = (LocalDate) variables.get("endDate");
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

    @PostMapping("/confirm/rh/{taskId}")
    public ResponseEntity<String> confirmRhTask1(@PathVariable UUID taskId, @Valid @RequestBody TaskConfirmationDTO confirmationDto) {
        String approvalStatus = confirmationDto.getApprovalStatus();
        String comment = confirmationDto.getComments();
        try {
            leaveService.confirmRhTask1(taskId, approvalStatus, comment);
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
}