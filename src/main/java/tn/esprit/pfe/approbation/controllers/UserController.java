package tn.esprit.pfe.approbation.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pfe.approbation.dtos.AuthorizationRequestDto;
import tn.esprit.pfe.approbation.dtos.LeaveRequestDto;
import tn.esprit.pfe.approbation.dtos.UserDto;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.Role;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import tn.esprit.pfe.approbation.services.IGestionUser;
import tn.esprit.pfe.approbation.services.LeaveService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/management")
@Tag(name = "Management")

public class UserController {
    @Autowired
    IGestionUser gestionUser;
    @Autowired
    private LeaveService leaveService;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/request")
    public ResponseEntity<String> requestLeave(@RequestBody LeaveRequestDto request) {
        String response = leaveService.handleLeaveRequest(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/requestAuthorization")
    public ResponseEntity<String> requestAutorisation(@RequestBody AuthorizationRequestDto request) {
        String response = leaveService.handleAuthorizationRequest(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests/all/{userId}")
    public List<LeaveRequest> getRequests(@PathVariable String userId) {
        return leaveService.getApprovedLeaveRequests(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        gestionUser.addUser(user);
        return ResponseEntity.ok(user);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{userId}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user == null || user.get() == null) {
            return ResponseEntity.notFound().build();
        }

        Path imagePath = Paths.get(user.get().getAvatar());
        byte[] imageData;
        try {
            imageData = Files.readAllBytes(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(imageData);
    }
    @PostMapping("/{userId}/image")
    public ResponseEntity<String> uploadUserImage(@PathVariable Integer userId, @RequestParam("avatar") MultipartFile file) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = optionalUser.get();
        try {
            String imagePath = saveImage(file);
            user.setAvatar(imagePath);
            userRepository.save(user);
            return ResponseEntity.ok(imagePath);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }
    private String saveImage(MultipartFile imageFile) {
        try {
            String uploadDir = "src/main/resources/static/images";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String uniqueId = UUID.randomUUID().toString().replace("-", "");
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            String modifiedFileName = uniqueId + "_" + originalFileName;
            Path filePath = uploadPath.resolve(modifiedFileName);
            int count = 1;
            while (Files.exists(filePath)) {
                modifiedFileName = uniqueId + "_" + count + "_" + originalFileName;
                filePath = uploadPath.resolve(modifiedFileName);
                count++;
            }
            Files.copy(imageFile.getInputStream(), filePath);
            return filePath.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save image", ex);
        }
    }
    @GetMapping("/team")
    public ResponseEntity<List<User>> getTeam(@AuthenticationPrincipal User authenticatedUser) {
        if (authenticatedUser.getRole() == Role.MANAGER) {
            List<User> team = gestionUser.getUsersByManager(authenticatedUser);
            return ResponseEntity.ok(team);
        } else if (authenticatedUser.getRole() != Role.MANAGER) {
            User manager = authenticatedUser.getManager();

            if (manager != null) {
                List<User> teamMembers = gestionUser.getUsersByManager(manager);

                teamMembers.remove(authenticatedUser);

                return ResponseEntity.ok(teamMembers);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }


}
