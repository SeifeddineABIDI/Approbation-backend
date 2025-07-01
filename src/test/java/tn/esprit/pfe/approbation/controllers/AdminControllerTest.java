package tn.esprit.pfe.approbation.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.pfe.approbation.dtos.ManagerDto;
import tn.esprit.pfe.approbation.dtos.UserDto;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.Role;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import tn.esprit.pfe.approbation.services.IGestionUser;

@ContextConfiguration(classes = {AdminController.class})
@ExtendWith(SpringExtension.class)
class AdminControllerTest {
    @Autowired
    private AdminController adminController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private IGestionUser iGestionUser;

    @MockBean
    private LeaveRequestRepository leaveRequestRepository;

    /**
     * Test {@link AdminController#getUsers()}.
     * <p>
     * Method under test: {@link AdminController#getUsers()}
     */
    @Test
    @DisplayName("Test getUsers()")
    @Tag("MaintainedBySeifeddineABIDI")
    void testGetUsers() {
        // Arrange and Act
        ResponseEntity<List<UserDto>> actualUsers = (new AdminController()).getUsers();
        // Assert
        HttpStatusCode statusCode = actualUsers.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertNull(actualUsers.getBody());
        assertEquals(500, actualUsers.getStatusCodeValue());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
        assertFalse(actualUsers.hasBody());
        assertTrue(actualUsers.getHeaders().isEmpty());
    }

    /**
     * Test {@link AdminController#getManagers()}.
     * <p>
     * Method under test: {@link AdminController#getManagers()}
     */
    @Test
    @DisplayName("Test getManagers()")
    @Tag("MaintainedBySeifeddineABIDI")
    void testGetManagers() {
        // Arrange and Act
        ResponseEntity<List<ManagerDto>> actualManagers = (new AdminController()).getManagers();
        // Assert
        HttpStatusCode statusCode = actualManagers.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertNull(actualManagers.getBody());
        assertEquals(500, actualManagers.getStatusCodeValue());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
        assertFalse(actualManagers.hasBody());
        assertTrue(actualManagers.getHeaders().isEmpty());
    }

    /**
     * Test {@link AdminController#searchUsers(String, String, String, String)}.
     * <ul>
     *   <li>Given {@link UserDto} (default constructor) Avatar is {@code Avatar}.</li>
     *   <li>Then content string a string.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminController#searchUsers(String, String, String, String)}
     */
    @Test
    @DisplayName("Test searchUsers(String, String, String, String); given UserDto (default constructor) Avatar is 'Avatar'; then content string a string")
    void testSearchUsers_givenUserDtoAvatarIsAvatar_thenContentStringAString() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setAvatar("Avatar");
        userDto.setEmail("jane.doe@example.org");
        userDto.setFirstName("Jane");
        userDto.setId(1);
        userDto.setLastName("Doe");
        userDto.setManagerMatricule("Manager Matricule");
        userDto.setMatricule("Matricule");
        userDto.setRole("Role");
        userDto.setSoldeConge(10.0d);

        ArrayList<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(userDto);
        when(iGestionUser.searchUsers(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
                Mockito.<String>any())).thenReturn(userDtoList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/admin/search");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(adminController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "<List><item><id>1</id><matricule>Matricule</matricule><firstName>Jane</firstName><lastName>Doe</lastName"
                                        + "><email>jane.doe@example.org</email><role>Role</role><soldeConge>10.0</soldeConge><managerMatricule>Manager"
                                        + " Matricule</managerMatricule><avatar>Avatar</avatar></item></List>"));
    }

    /**
     * Test {@link AdminController#searchUsers(String, String, String, String)}.
     * <ul>
     *   <li>Then content string {@code <List/>}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminController#searchUsers(String, String, String, String)}
     */
    @Test
    @DisplayName("Test searchUsers(String, String, String, String); then content string '<List/>'")
    void testSearchUsers_thenContentStringList() throws Exception {
        // Arrange
        when(iGestionUser.searchUsers(Mockito.<String>any(), Mockito.<String>any(), Mockito.<String>any(),
                Mockito.<String>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/admin/search");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(adminController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }

    /**
     * Test {@link AdminController#updateUser(Integer, UserDto, MultipartFile)}.
     * <p>
     * Method under test: {@link AdminController#updateUser(Integer, UserDto, MultipartFile)}
     */
    @Test
    @DisplayName("Test updateUser(Integer, UserDto, MultipartFile)")
    @Tag("MaintainedBySeifeddineABIDI")
    void testUpdateUser() throws Exception {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setAvatar("Avatar");
        userDto.setEmail("jane.doe@example.org");
        userDto.setFirstName("Jane");
        userDto.setId(1);
        userDto.setLastName("Doe");
        userDto.setManagerMatricule("Manager Matricule");
        userDto.setMatricule("Matricule");
        userDto.setRole("Role");
        userDto.setSoldeConge(10.0d);
        when(iGestionUser.updateUser(Mockito.<Integer>any(), Mockito.<UserDto>any(), Mockito.<MultipartFile>any()))
                .thenReturn(userDto);
        MockHttpServletRequestBuilder putResult = MockMvcRequestBuilders.put("/api/v1/admin/update/{userId}", 1);
        MockHttpServletRequestBuilder requestBuilder = putResult.param("imageFile",
                String.valueOf(new MockMultipartFile("Name", (InputStream) null)));

        // Act and Assert
        MockMvcBuilders.standaloneSetup(adminController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "<UserDto><id>1</id><matricule>Matricule</matricule><firstName>Jane</firstName><lastName>Doe</lastName"
                                        + "><email>jane.doe@example.org</email><role>Role</role><soldeConge>10.0</soldeConge><managerMatricule>Manager"
                                        + " Matricule</managerMatricule><avatar>Avatar</avatar></UserDto>"));
    }

    /**
     * Test {@link AdminController#getUserByMatricule(String)}.
     * <ul>
     *   <li>Given {@link User#User()} Avatar is {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminController#getUserByMatricule(String)}
     */
    @Test
    @DisplayName("Test getUserByMatricule(String); given User() Avatar is 'null'")
    @Tag("MaintainedBySeifeddineABIDI")
    void testGetUserByMatricule_givenUserAvatarIsNull() throws Exception {
        // Arrange
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

        User manager3 = new User();
        manager3.setAvatar("Avatar");
        manager3.setEmail("jane.doe@example.org");
        manager3.setFirstName("Jane");
        manager3.setId(1);
        manager3.setLastName("Doe");
        manager3.setManager(manager2);
        manager3.setMatricule("Matricule");
        manager3.setOccurAutorisation(1);
        manager3.setOnLeave(true);
        manager3.setPassword("iloveyou");
        manager3.setRole(Role.USER);
        manager3.setSoldeAutorisation(1);
        manager3.setSoldeConge(10.0d);
        manager3.setTokens(new ArrayList<>());

        User manager4 = new User();
        manager4.setAvatar("Avatar");
        manager4.setEmail("jane.doe@example.org");
        manager4.setFirstName("Jane");
        manager4.setId(1);
        manager4.setLastName("Doe");
        manager4.setManager(manager3);
        manager4.setMatricule("Matricule");
        manager4.setOccurAutorisation(1);
        manager4.setOnLeave(true);
        manager4.setPassword("iloveyou");
        manager4.setRole(Role.USER);
        manager4.setSoldeAutorisation(1);
        manager4.setSoldeConge(10.0d);
        manager4.setTokens(new ArrayList<>());

        User user = new User();
        user.setAvatar(null);
        user.setEmail("jane.doe@example.org");
        user.setFirstName("Jane");
        user.setId(1);
        user.setLastName("Doe");
        user.setManager(manager4);
        user.setMatricule("Matricule");
        user.setOccurAutorisation(1);
        user.setOnLeave(true);
        user.setPassword("iloveyou");
        user.setRole(Role.USER);
        user.setSoldeAutorisation(1);
        user.setSoldeConge(10.0d);
        user.setTokens(new ArrayList<>());
        when(userRepository.findByMatricule(Mockito.<String>any())).thenReturn(user);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/admin/getUserByMat/{matricule}",
                "Matricule");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(adminController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "<UserDto><id>1</id><matricule>Matricule</matricule><firstName>Jane</firstName><lastName>Doe</lastName"
                                        + "><email>jane.doe@example.org</email><role>USER</role><soldeConge>10.0</soldeConge><managerMatricule"
                                        + ">Matricule</managerMatricule><avatar/></UserDto>"));
    }

    /**
     * Test {@link AdminController#getUserByMatricule(String)}.
     * <p>
     * Method under test: {@link AdminController#getUserByMatricule(String)}
     */
    @Test
    @DisplayName("Test getUserByMatricule(String)")
    @Tag("MaintainedBySeifeddineABIDI")
    void testGetUserByMatricule() throws Exception {
        // Arrange
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

        User manager3 = new User();
        manager3.setAvatar("Avatar");
        manager3.setEmail("jane.doe@example.org");
        manager3.setFirstName("Jane");
        manager3.setId(1);
        manager3.setLastName("Doe");
        manager3.setManager(manager2);
        manager3.setMatricule("Matricule");
        manager3.setOccurAutorisation(1);
        manager3.setOnLeave(true);
        manager3.setPassword("iloveyou");
        manager3.setRole(Role.USER);
        manager3.setSoldeAutorisation(1);
        manager3.setSoldeConge(10.0d);
        manager3.setTokens(new ArrayList<>());

        User manager4 = new User();
        manager4.setAvatar("Avatar");
        manager4.setEmail("jane.doe@example.org");
        manager4.setFirstName("Jane");
        manager4.setId(1);
        manager4.setLastName("Doe");
        manager4.setManager(manager3);
        manager4.setMatricule("Matricule");
        manager4.setOccurAutorisation(1);
        manager4.setOnLeave(true);
        manager4.setPassword("iloveyou");
        manager4.setRole(Role.USER);
        manager4.setSoldeAutorisation(1);
        manager4.setSoldeConge(10.0d);
        manager4.setTokens(new ArrayList<>());

        User user = new User();
        user.setAvatar("Avatar");
        user.setEmail("jane.doe@example.org");
        user.setFirstName("Jane");
        user.setId(1);
        user.setLastName("Doe");
        user.setManager(manager4);
        user.setMatricule("Matricule");
        user.setOccurAutorisation(1);
        user.setOnLeave(true);
        user.setPassword("iloveyou");
        user.setRole(Role.USER);
        user.setSoldeAutorisation(1);
        user.setSoldeConge(10.0d);
        user.setTokens(new ArrayList<>());
        when(userRepository.findByMatricule(Mockito.<String>any())).thenReturn(user);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/admin/getUserByMat/{matricule}",
                "Matricule");
        // Act and Assert
        MockMvcBuilders.standaloneSetup(adminController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "<UserDto><id>1</id><matricule>Matricule</matricule><firstName>Jane</firstName><lastName>Doe</lastName"
                                        + "><email>jane.doe@example.org</email><role>USER</role><soldeConge>10.0</soldeConge><managerMatricule"
                                        + ">Matricule</managerMatricule><avatar>Avatar</avatar></UserDto>"));
    }

    /**
     * Test {@link AdminController#deleteUser(Integer)}.
     * <p>
     * Method under test: {@link AdminController#deleteUser(Integer)}
     */
    @Test
    @DisplayName("Test deleteUser(Integer)")
    @Tag("MaintainedBySeifeddineABIDI")
    void testDeleteUser() {
        // Arrange and Act
        ResponseEntity<Void> actualDeleteUserResult = (new AdminController()).deleteUser(1);
        // Assert
        HttpStatusCode statusCode = actualDeleteUserResult.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertNull(actualDeleteUserResult.getBody());
        assertEquals(500, actualDeleteUserResult.getStatusCodeValue());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
        assertFalse(actualDeleteUserResult.hasBody());
        assertTrue(actualDeleteUserResult.getHeaders().isEmpty());
    }

    /**
     * Test {@link AdminController#getRequests(String, String, String, String, Pageable)}
     * <ul>
     *   <li>When {@code Sort Field}.</li>
     * </ul>
     * <p>
     * Method under test: {@link AdminController#getRequests(String, String, String, String, Pageable)}
     */
    @Test
    @DisplayName("Test getRequests when sortField and sortDirection are provided")
    void testGetRequests_withSorting() {
        // Arrange
        String sortField = "id";
        String sortDirection = "asc";

        Pageable originalPageable = PageRequest.of(0, 5); // sera remplacé avec le tri
        Pageable expectedPageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, sortField));

        Page<LeaveRequest> mockedPage = new PageImpl<>(List.of()); // ou une vraie liste si tu veux

        Mockito.when(leaveRequestRepository.findAll(expectedPageable))
                .thenReturn(mockedPage);

        // Act
        ResponseEntity<Page<LeaveRequest>> response = adminController.getRequests(null, sortField, sortDirection, null, originalPageable);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockedPage, response.getBody());
        Mockito.verify(leaveRequestRepository).findAll(expectedPageable);
    }
}
