package tn.esprit.pfe.approbation.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pfe.approbation.DTOs.LeaveRequestDto;
import tn.esprit.pfe.approbation.Entities.User;
import tn.esprit.pfe.approbation.Services.IGestionUser;
import tn.esprit.pfe.approbation.Services.LeaveService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IGestionUser gestionUser;
    @Autowired
    private LeaveService leaveService;

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
}
