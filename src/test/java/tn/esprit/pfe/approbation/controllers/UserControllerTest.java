package tn.esprit.pfe.approbation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.pfe.approbation.dtos.AuthorizationRequestDto;
import tn.esprit.pfe.approbation.dtos.UserFullNameDto;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.Role;
import tn.esprit.pfe.approbation.entities.TypeConge;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import tn.esprit.pfe.approbation.services.IGestionUser;
import tn.esprit.pfe.approbation.services.LeaveService;

@ContextConfiguration(classes = {UserController.class})
@ExtendWith(SpringExtension.class)
class UserControllerTest {

    @MockBean
    private LeaveService leaveService;

    @Autowired
    private UserController userController;

    @MockBean
    private IGestionUser iGestionUser;

    @MockBean
    private LeaveRequestRepository leaveRequestRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("Test getRequests(String); given TypeConge (default constructor) Id is one; then content string a string")
    void testGetRequests_givenTypeCongeIdIsOne_thenContentStringAString() throws Exception {
        // Arrange
        TypeConge type = new TypeConge();
        type.setId(1);
        type.setLeaveRequests(new ArrayList<>());
        type.setName("Name");

        User manager = new User();
        manager.setAvatar("Avatar");
        manager.setEmail("jane.doe@example.org");
        manager.setFirstName("Jane");
        manager.setId(1);
        manager.setLastName("Doe");
        manager.setManager(new User());
        manager.setMatricule("Matricule");
        manager.setOccurAutorisation(1);
        manager.setOnLeave(true);
        manager.setPassword("iloveyou");
        manager.setRole(Role.USER);
        manager.setSoldeAutorisation(1);
        manager.setSoldeConge(10.0d);
        manager.setTokens(new ArrayList<>());

        User manager2 = new User();
        manager2.setAvatar("Avatar");
        manager2.setEmail("jane.doe@example.org");
        manager2.setFirstName("Jane");
        manager2.setId(1);
        manager2.setLastName("Doe");
        manager2.setManager(manager);
        manager2.setMatricule("Matricule");
        manager2.setOccurAutorisation(1);
        manager2.setOnLeave(true);
        manager2.setPassword("iloveyou");
        manager2.setRole(Role.USER);
        manager2.setSoldeAutorisation(1);
        manager2.setSoldeConge(10.0d);
        manager2.setTokens(new ArrayList<>());

        User user = new User();
        user.setAvatar("Avatar");
        user.setEmail("jane.doe@example.org");
        user.setFirstName("Jane");
        user.setId(1);
        user.setLastName("Doe");
        user.setManager(manager2);
        user.setMatricule("Matricule");
        user.setOccurAutorisation(1);
        user.setOnLeave(true);
        user.setPassword("iloveyou");
        user.setRole(Role.USER);
        user.setSoldeAutorisation(1);
        user.setSoldeConge(10.0d);
        user.setTokens(new ArrayList<>());

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setApproved(true);
        leaveRequest.setBackAfterMidday(true);
        leaveRequest.setEndDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        leaveRequest.setGoAfterMidday(true);
        leaveRequest.setId(1L);
        leaveRequest.setProcInstId("42");
        leaveRequest.setRequestDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        leaveRequest.setStartDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        leaveRequest.setType(type);
        leaveRequest.setUser(user);

        ArrayList<LeaveRequest> leaveRequestList = new ArrayList<>();
        leaveRequestList.add(leaveRequest);
        when(leaveService.getApprovedLeaveRequests(Mockito.<String>any())).thenReturn(leaveRequestList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/management/requests/all/{userId}", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string(containsString("<soldeConge>10.0</soldeConge>")));
    }

    @Test
    @DisplayName("Test getTeamLeaves(User); given TypeConge (default constructor) Id is one; then content string a string")
    void testGetTeamLeaves_givenTypeCongeIdIsOne_thenContentStringAString() throws Exception {
        // Arrange
        TypeConge type = new TypeConge();
        type.setId(1);
        type.setLeaveRequests(new ArrayList<>());
        type.setName("Name");

        User manager = new User();
        manager.setAvatar("Avatar");
        manager.setEmail("jane.doe@example.org");
        manager.setFirstName("Jane");
        manager.setId(1);
        manager.setLastName("Doe");
        manager.setManager(new User());
        manager.setMatricule("Matricule");
        manager.setOccurAutorisation(1);
        manager.setOnLeave(true);
        manager.setPassword("iloveyou");
        manager.setRole(Role.USER);
        manager.setSoldeAutorisation(1);
        manager.setSoldeConge(10.0d);
        manager.setTokens(new ArrayList<>());

        User manager2 = new User();
        manager2.setAvatar("Avatar");
        manager2.setEmail("jane.doe@example.org");
        manager2.setFirstName("Jane");
        manager2.setId(1);
        manager2.setLastName("Doe");
        manager2.setManager(manager);
        manager2.setMatricule("Matricule");
        manager2.setOccurAutorisation(1);
        manager2.setOnLeave(true);
        manager2.setPassword("iloveyou");
        manager2.setRole(Role.USER);
        manager2.setSoldeAutorisation(1);
        manager2.setSoldeConge(10.0d);
        manager2.setTokens(new ArrayList<>());

        User user = new User();
        user.setAvatar("Avatar");
        user.setEmail("jane.doe@example.org");
        user.setFirstName("Jane");
        user.setId(1);
        user.setLastName("Doe");
        user.setManager(manager2);
        user.setMatricule("Matricule");
        user.setOccurAutorisation(1);
        user.setOnLeave(true);
        user.setPassword("iloveyou");
        user.setRole(Role.USER);
        user.setSoldeAutorisation(1);
        user.setSoldeConge(10.0d);
        user.setTokens(new ArrayList<>());

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setApproved(true);
        leaveRequest.setBackAfterMidday(true);
        leaveRequest.setEndDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        leaveRequest.setGoAfterMidday(true);
        leaveRequest.setId(1L);
        leaveRequest.setProcInstId("42");
        leaveRequest.setRequestDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        leaveRequest.setStartDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        leaveRequest.setType(type);
        leaveRequest.setUser(user);

        ArrayList<LeaveRequest> leaveRequestList = new ArrayList<>();
        leaveRequestList.add(leaveRequest);
        when(iGestionUser.getTeamLeaves(Mockito.<User>any())).thenReturn(leaveRequestList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/management/leaves");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string(containsString("<soldeConge>10.0</soldeConge>")));
    }

    @Test
    @DisplayName("Test requestAuthorization success")
    void testRequestAuthorization_Success() throws Exception {
        when(leaveService.handleAuthorizationRequest(any(AuthorizationRequestDto.class)))
                .thenReturn("Authorization request processed");

        String jsonRequest = "{\"someField\":\"someValue\"}"; // Adjust based on actual AuthorizationRequestDto structure

        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(MockMvcRequestBuilders.post("/api/v1/management/requestAuthorization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Authorization request processed"));
    }

    @Test
    @DisplayName("Test getUserImage when user exists")
    void testGetUserImage_UserExists() throws Exception {
        User user = new User();
        user.setId(1);
        user.setAvatar("/app/images/test-image.jpg");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Mock the Files.readAllBytes() call
        Path mockPath = mock(Path.class);
        when(Paths.get("/app/images/test-image.jpg")).thenReturn(mockPath);
        when(Files.readAllBytes(mockPath)).thenReturn("test image content".getBytes());

        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(MockMvcRequestBuilders.get("/api/v1/management/1/image"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.IMAGE_JPEG));
    }

    @Test
    @DisplayName("Test getUserImage when user not found")
    void testGetUserImage_UserNotFound() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(MockMvcRequestBuilders.get("/api/v1/management/1/image"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Test uploadUserImage success")
    void testUploadUserImage_Success() throws Exception {
        User user = new User();
        user.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        MockMultipartFile file = new MockMultipartFile(
                "avatar",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(MockMvcRequestBuilders.multipart("/api/v1/management/1/image")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(containsString("images"))); // Use containsString instead
    }

    @Test
    @DisplayName("Test uploadUserImage user not found")
    void testUploadUserImage_UserNotFound() throws Exception {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile(
                "avatar",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(MockMvcRequestBuilders.multipart("/api/v1/management/1/image")
                        .file(file))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("User not found"));
    }

    @Test
    @DisplayName("Test updateName success")
    void testUpdateName_Success() throws Exception {
        User user = new User();
        user.setMatricule("123");
        user.setFirstName("OldFirst");
        user.setLastName("OldLast");
        user.setRole(Role.USER); // Set a valid role

        when(userRepository.findByMatricule("123")).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserFullNameDto dto = new UserFullNameDto();
        dto.setFirstName("NewFirst");
        dto.setLastName("NewLast");

        String jsonRequest = "{\"firstName\":\"NewFirst\",\"lastName\":\"NewLast\"}";

        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(MockMvcRequestBuilders.put("/api/v1/management/updateName/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)) // Match actual response type
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("NewFirst"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("NewLast"));
    }

    @Test
    @DisplayName("Test updateName user not found")
    void testUpdateName_UserNotFound() throws Exception {
        when(userRepository.findByMatricule("123")).thenReturn(null);

        UserFullNameDto dto = new UserFullNameDto();
        dto.setFirstName("NewFirst");
        dto.setLastName("NewLast");

        String jsonRequest = "{\"firstName\":\"NewFirst\",\"lastName\":\"NewLast\"}";

        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(MockMvcRequestBuilders.put("/api/v1/management/updateName/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isNotFound ());
    }
}
