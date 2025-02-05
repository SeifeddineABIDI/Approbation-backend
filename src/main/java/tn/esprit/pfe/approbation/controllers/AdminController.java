package tn.esprit.pfe.approbation.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pfe.approbation.dtos.ManagerDto;
import tn.esprit.pfe.approbation.dtos.UserDto;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import tn.esprit.pfe.approbation.services.IGestionUser;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    IGestionUser gestionUser;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<UserDto>> getUsers() {
        try {
            List<User> users = gestionUser.findAll();
            List<UserDto> userDtos = users.stream().map(UserDto::fromEntity).toList();
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/managers")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<ManagerDto>> getManagers() {
        try {
            List<ManagerDto> managers = gestionUser.getManagers();
            return ResponseEntity.ok(managers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/getUserByMat/{matricule}")
    public UserDto getUserByMatricule(@PathVariable String matricule) {
        try {
            User user = userRepository.findByMatricule(matricule);
            if (user != null) {
                return UserDto.fromEntity(user);
            } else {
                // Handle case when user is not found (optional, depending on your use case)
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            return null;
        }
    }


    @GetMapping("/search")
    @PreAuthorize("hasAuthority('admin:read')")
    public ResponseEntity<List<UserDto>> searchUsers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String matricule) {
        List<UserDto> users = gestionUser.searchUsers(firstName, lastName, email, matricule);
        return ResponseEntity.ok(users);
    }
    @Transactional
    @PutMapping("/update/{userId}")
    @CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend
    @PreAuthorize("hasAuthority('admin:update')")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Integer userId,
            @ModelAttribute  UserDto userDto,
            @RequestPart(value = "avatar", required = false) MultipartFile imageFile) {
        System.out.println("Received firstName: " + userDto.getFirstName());

        UserDto updatedUser = gestionUser.updateUser(userId, userDto, imageFile);
        return ResponseEntity.ok(updatedUser);
    }
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    @Transactional
    @DeleteMapping("/delete/{userId}")
    @CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        try {
            gestionUser.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.error("User not found: {}", userId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Internal server error while deleting user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Hidden
    public String post() {
        return "POST:: admin controller";
    }
    @PutMapping
    @PreAuthorize("hasAuthority('admin:update')")
    @Hidden
    public String put() {
        return "PUT:: admin controller";
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority('admin:delete')")
    @Hidden
    public String delete() {
        return "DELETE:: admin controller";
    }
}