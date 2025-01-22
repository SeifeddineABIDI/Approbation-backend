package tn.esprit.pfe.approbation.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pfe.approbation.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserRepository userRepository;
    private final AuthenticationService service;
    private Boolean userExists;
    private String imagePath= "";
    private static final String IMAGE_DIRECTORY = "src/main/resources/static/images";

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("avatar") MultipartFile avatar,
            HttpServletRequest httpRequest
    ) {
        userExists = userRepository.existsByEmail(email);
        if(!userExists){
            imagePath = saveImage(avatar);}
        RegisterRequest request = RegisterRequest.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .password(password)
                .role(role)
                .avatar(imagePath)
                .build();
        AuthenticationResponse response = service.register(request,httpRequest);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(service.authenticate(request,httpRequest));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
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

    @GetMapping("/api/client-ip")
    public String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}