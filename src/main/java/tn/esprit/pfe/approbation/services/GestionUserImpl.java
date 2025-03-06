package tn.esprit.pfe.approbation.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pfe.approbation.dtos.ManagerDto;
import tn.esprit.pfe.approbation.dtos.UserDto;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.Role;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.entities.UserSpecification;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tn.esprit.pfe.approbation.token.Token;
import tn.esprit.pfe.approbation.token.TokenRepository;

import java.time.Year;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GestionUserImpl implements IGestionUser {

    @Autowired
    UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(GestionUserImpl.class);
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(User user) {
        if (user.getMatricule() == null || user.getMatricule().isEmpty()) {
            user.setMatricule(generateMatricule());
        }
        return userRepository.save(user);
        }

    public String generateMatricule() {
        int currentYearShort = Year.now().getValue() % 100;
        int currentMonth = LocalDate.now().getMonthValue();

        String lastMatricule = userRepository.findLastMatricule();
        logger.info("Last matricule from database: {}", lastMatricule);
        int nextSequence = 1;
        if (lastMatricule != null && !lastMatricule.isEmpty()) {
            try {
                if (lastMatricule.matches("^\\d{4}EMP\\d{3}$")) {
                    int yearFromLastMatricule = Integer.parseInt(lastMatricule.substring(0, 2));
                    int monthFromLastMatricule = Integer.parseInt(lastMatricule.substring(2, 4));
                    int sequenceFromLastMatricule = Integer.parseInt(lastMatricule.substring(7));
                    logger.debug("Parsed Year: {}, Month: {}, Sequence: {}", yearFromLastMatricule, monthFromLastMatricule, sequenceFromLastMatricule);
                    if (yearFromLastMatricule == currentYearShort && monthFromLastMatricule == currentMonth) {
                        nextSequence = sequenceFromLastMatricule + 1;
                    } else {
                        logger.info("Year or month mismatch. Resetting sequence.");
                    }
                } else {
                    logger.warn("Matricule format mismatch. Ignoring: {}", lastMatricule);
                }
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                logger.error("Error parsing last matricule: {}. Exception: {}", lastMatricule, e.getMessage());
            }
        } else {
            logger.info("No matricule found. Starting from sequence 1.");
        }

        String generatedMatricule = String.format("%02d%02dEMP%03d", currentYearShort, currentMonth, nextSequence);
        logger.info("Generated matricule: {}", generatedMatricule);

        return generatedMatricule;
    }

    public List<ManagerDto> getManagers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.MANAGER)
                .map(user -> new ManagerDto(
                        user.getFirstName() + " " + user.getLastName(),
                        user.getMatricule()
                ))
                .collect(Collectors.toList());
    }

    public List<UserDto> searchUsers(String firstName, String lastName, String email, String matricule) {
        List<User> users = userRepository.searchUsers(firstName, lastName, email, matricule);
        return users.stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(Integer userId, UserDto userDto, MultipartFile imageFile) {
        System.out.println("=== Update User Debug ===");
        System.out.println("Updating user ID: " + userId);
        System.out.println("Received DTO data:");
        System.out.println("Manager Matricule: " + userDto.getManagerMatricule());
        User user = userRepository.findUserById(userId);
        if (userDto.getFirstName() != null && !userDto.getFirstName().isEmpty()) {user.setFirstName(userDto.getFirstName());}
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            user.setLastName(userDto.getLastName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getMatricule() != null && !userDto.getMatricule().isEmpty()) {
            user.setMatricule(userDto.getMatricule());
        }
        if (userDto.getRole() != null && !userDto.getRole().isEmpty()) {
            try {
                user.setRole(Role.valueOf(userDto.getRole()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + userDto.getRole());
            }
        }
        if (userDto.getManagerMatricule() != null && !userDto.getManagerMatricule().isEmpty()) {
            System.out.println("Looking up manager with matricule: " + userDto.getManagerMatricule());
            User manager = userRepository.findByMatricule(userDto.getManagerMatricule());
            if (manager == null) {
                System.out.println("ERROR: Manager not found with matricule: " + userDto.getManagerMatricule());
            }
            System.out.println("Found manager: " + manager);
            user.setManager(manager);
            System.out.println("Manager set on user object: " + user.getManager());
        } else {
            System.out.println("No manager matricule provided in the update request");
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            user.setAvatar(imagePath);
        }else {
            System.out.println("No avatar provided, skipping update for avatar.");
        }
        user.setSoldeConge(userDto.getSoldeConge());
        System.out.println("user: " + user);
        User updatedUser = userRepository.save(user);
        return UserDto.fromEntity(updatedUser);
    }
    private static final Logger log = LoggerFactory.getLogger(GestionUserImpl.class);
    @Override
    public void deleteUser(Integer userId) {
        try {
            log.info("Deleting user: {}", userId);
            userRepository.deleteUserById(userId);
        } catch (EntityNotFoundException e) {
            log.error("User not found: {}", userId);
            throw e;
        } catch (Exception e) {
            log.error("Error deleting user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Error deleting user", e);
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
        }   catch (IOException ex) {
            throw new RuntimeException("Failed to save image", ex);
            }
    }
    public List<User> getUsersByManager(User manager) {
        return userRepository.findByManager(manager);
    }
    public List<LeaveRequest> getTeamLeaves(User authenticatedUser) {
        List<User> team;
        if (authenticatedUser.getRole().toString()=="MANAGER") {
            team = getUsersByManager(authenticatedUser);
        } else {
            User manager = authenticatedUser.getManager();
            if (manager != null) {
                team = getUsersByManager(manager);
            } else {
                team = List.of();
            }
        }
        return leaveRequestRepository.findByUserIn(team);
    }
}
