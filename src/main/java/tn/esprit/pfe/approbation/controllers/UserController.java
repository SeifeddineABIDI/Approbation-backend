package tn.esprit.pfe.approbation.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pfe.approbation.dtos.LeaveRequestDto;
import tn.esprit.pfe.approbation.dtos.UserDto;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
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
        // Retrieve user by ID
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

            // Return image data as response
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
            // Save the image and get its path
            String imagePath = saveImage(file);

            // Update user avatar path in database
            user.setAvatar(imagePath);
            userRepository.save(user);

            return ResponseEntity.ok(imagePath); // Return the new image path
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
        }
    }
    private String saveImage(MultipartFile imageFile) {
        try {
            // Get the path to the resources/static directory
            String uploadDir = "src/main/resources/static/images";

            // Create the directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Get the original filename of the uploaded file
            String originalFileName = StringUtils.cleanPath(imageFile.getOriginalFilename());

            // Generate a unique identifier
            String uniqueId = UUID.randomUUID().toString().replace("-", "");

            // Extract file extension
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

            // Append unique identifier to the filename
            String modifiedFileName = uniqueId + "_" + originalFileName;

            // Get the path to save the image
            Path filePath = uploadPath.resolve(modifiedFileName);

            // Check if the file with the modified name already exists
            int count = 1;
            while (Files.exists(filePath)) {
                modifiedFileName = uniqueId + "_" + count + "_" + originalFileName;
                filePath = uploadPath.resolve(modifiedFileName);
                count++;
            }

            // Save the image to the specified path
            Files.copy(imageFile.getInputStream(), filePath);

            // Return the path where the image is saved
            return filePath.toString();
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save image", ex);
        }
    }
}
