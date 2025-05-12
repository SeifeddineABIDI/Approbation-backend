package tn.esprit.pfe.approbation.auth;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.pfe.approbation.dtos.ResetPasswordRequest;
import tn.esprit.pfe.approbation.entities.PasswordReset;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.PasswordResetRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import tn.esprit.pfe.approbation.services.EmailService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserRepository userRepository;
    private final AuthenticationService service;
    private Boolean userExists;
    private String imagePath= "";
    private static final String IMAGE_DIRECTORY = "src/main/resources/static/images";
    @Autowired
    PasswordResetRepository passwordResetRepository;
    @Autowired
    EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private final RecaptchaService recaptchaService;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("role") String role,
            @RequestParam("avatar") MultipartFile avatar,
            @RequestParam(value = "managerMatricule", required = false) String managerMatricule,
            HttpServletRequest httpRequest
    ) {
        userExists = userRepository.existsByEmail(email);
        if(!userExists){
            imagePath = saveImage(avatar);}
        User manager = userRepository.findByMatricule(managerMatricule);

        RegisterRequest request = RegisterRequest.builder()
                .firstname(firstname)
                .lastname(lastname)
                .email(email)
                .password(password)
                .role(role)
                .avatar(imagePath)
                .manager(manager)
                .build();
        AuthenticationResponse response = service.register(request,httpRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletRequest httpRequest
    ) {
        System.out.println("Received Request: " + request);

        // Check if reCAPTCHA token is present and valid
        if (request.getRecaptchaToken() == null || request.getRecaptchaToken().isEmpty()) {
            System.out.println("reCAPTCHA token is missing");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthenticationResponse.builder()
                            .error("reCAPTCHA token is required")
                            .build());
        }

        if (!recaptchaService.verifyRecaptcha(request.getRecaptchaToken())) {
            System.out.println("reCAPTCHA verification failed for token: " + request.getRecaptchaToken());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationResponse.builder()
                            .error("reCAPTCHA verification failed")
                            .build());
        }

        try {
            AuthenticationResponse response = service.authenticate(request, httpRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthenticationResponse.builder()
                            .error("Authentication failed: " + e.getMessage())
                            .build());
        }
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
            String uploadDir = "src/main/resources/static/images";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String uniqueId = UUID.randomUUID().toString().replace("-", "");
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

    @GetMapping("/api/client-ip")
    public String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) throws MessagingException {
        String email = request.get("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        passwordResetRepository.findByUser(user).ifPresent(passwordResetRepository::delete);
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (passwordResetRepository.existsByToken(token));
        PasswordReset resetToken = new PasswordReset(token, user, LocalDateTime.now().plusHours(1));
        passwordResetRepository.save(resetToken);
        String resetLink = "http://localhost:4200/reset-password/?token=" + token;
        Map<String, Object> variables = new HashMap<>();
        variables.put("resetLink", resetLink);
        emailService.sendEmail(user.getEmail(),
                "Reset Your Password",
                "reset-password",
                variables);

        return ResponseEntity.ok("Password reset link sent!");
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        Optional<PasswordReset> resetToken = passwordResetRepository.findByToken(token);

        if (resetToken.isEmpty() || resetToken.get().getExpiryDate().isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid or expired token");
        }

        return ResponseEntity.ok().body("Valid token");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        PasswordReset tokenEntity = passwordResetRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token"));
        User user = tokenEntity.getUser();
        if (!user.getEmail().equals(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Email does not match the token's user."));        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        passwordResetRepository.delete(tokenEntity);
        return ResponseEntity.ok(Collections.singletonMap("message", "Password has been reset successfully."));
    }
}