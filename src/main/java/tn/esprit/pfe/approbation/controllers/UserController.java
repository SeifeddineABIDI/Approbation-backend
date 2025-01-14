package tn.esprit.pfe.approbation.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pfe.approbation.dtos.LeaveRequestDto;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import tn.esprit.pfe.approbation.services.IGestionUser;
import tn.esprit.pfe.approbation.services.LeaveService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IGestionUser gestionUser;
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers(){
        try {
            List<User> users = gestionUser.findAll();
            users.forEach(user -> System.out.println(user));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/request")
    public ResponseEntity<String> requestLeave(@RequestBody LeaveRequestDto request) {
        String response = leaveService.handleLeaveRequest(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/requests/all/{userId}")
    public List<LeaveRequest> getRequests(@PathVariable String userId){
        return leaveService.getApprovedLeaveRequests(userId);}

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user){
        gestionUser.addUser(user);
        return ResponseEntity.ok(user);
    }
}
