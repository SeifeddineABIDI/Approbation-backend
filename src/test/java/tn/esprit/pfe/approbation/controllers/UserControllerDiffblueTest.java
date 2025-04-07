package tn.esprit.pfe.approbation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.containsString;
import com.diffblue.cover.annotations.MethodsUnderTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
import tn.esprit.pfe.approbation.dtos.LeaveRequestDto;
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
class UserControllerDiffblueTest {

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

    /**
     * Test {@link UserController#getRequests(String)}.
     * <ul>
     *   <li>Given {@link TypeConge} (default constructor) Id is one.</li>
     *   <li>Then content string a string.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserController#getRequests(String)}
     */
    @Test
    @DisplayName("Test getRequests(String); given TypeConge (default constructor) Id is one; then content string a string")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"java.util.List UserController.getRequests(String)"})
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
                .andExpect(MockMvcResultMatchers.content()
                        .string("<List><item><id>1</id><requestDate>1970</requestDate><requestDate>1</requestDate><requestDate>1<"
                                + "/requestDate><requestDate>0</requestDate><requestDate>0</requestDate><startDate>1970</startDate>"
                                + "<startDate>1</startDate><startDate>1</startDate><startDate>0</startDate><startDate>0</startDate><endDate"
                                + ">1970</endDate><endDate>1</endDate><endDate>1</endDate><endDate>0</endDate><endDate>0</endDate><approved"
                                + ">true</approved><procInstId>42</procInstId><goAfterMidday>true</goAfterMidday><backAfterMidday>true<"
                                + "/backAfterMidday><type><id>1</id><name>Name</name></type><user><id>1</id><matricule>Matricule</matricule"
                                + "><firstName>Jane</firstName><lastName>Doe</lastName><email>jane.doe@example.org</email><avatar>Avatar"
                                + "</avatar><soldeConge>10.0</soldeConge><role>USER</role><soldeAutorisation>1</soldeAutorisation>"
                                + "<occurAutorisation>1</occurAutorisation><enabled>true</enabled><authorities><authorities><authority"
                                + ">ROLE_USER</authority></authorities></authorities><username>jane.doe@example.org</username><accountNonExpired"
                                + ">true</accountNonExpired><credentialsNonExpired>true</credentialsNonExpired><onLeave>true</onLeave>"
                                + "<accountNonLocked>true</accountNonLocked><manager><id>1</id><matricule>Matricule</matricule><firstName"
                                + ">Jane</firstName><lastName>Doe</lastName><email>jane.doe@example.org</email><avatar>Avatar</avatar>"
                                + "<role>USER</role><soldeAutorisation>1</soldeAutorisation><occurAutorisation>1</occurAutorisation>"
                                + "<enabled>true</enabled><authorities><authorities><authority>ROLE_USER</authority></authorities><"
                                + "/authorities><username>jane.doe@example.org</username><accountNonExpired>true</accountNonExpired>"
                                + "<credentialsNonExpired>true</credentialsNonExpired><accountNonLocked>true</accountNonLocked></manager"
                                + "></user></item></List>"));
    }

    /**
     * Test {@link UserController#getRequests(String)}.
     * <ul>
     *   <li>Then content string {@code <List/>}.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserController#getRequests(String)}
     */
    @Test
    @DisplayName("Test getRequests(String); then content string '<List/>'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"java.util.List UserController.getRequests(String)"})
    void testGetRequests_thenContentStringList() throws Exception {
        // Arrange
        when(leaveService.getApprovedLeaveRequests(Mockito.<String>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/v1/management/requests/all/{userId}", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }

    /**
     * Test {@link UserController#addUser(User)}.
     * <p>
     * Method under test: {@link UserController#addUser(User)}
     */
    @Test
    @DisplayName("Test addUser(User)")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"org.springframework.http.ResponseEntity UserController.addUser(User)"})
    void testAddUser() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   jakarta.servlet.ServletException: Request processing failed: org.springframework.http.converter.HttpMessageConversionException: Type definition error: [simple type, class org.springframework.security.core.GrantedAuthority]
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
        //   org.springframework.http.converter.HttpMessageConversionException: Type definition error: [simple type, class org.springframework.security.core.GrantedAuthority]
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
        //   com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `org.springframework.security.core.GrantedAuthority` (no Creators, like default constructor, exist): abstract types either need to be mapped to concrete types, have custom deserializer, or contain additional type information
        //    at [Source: (org.springframework.util.StreamUtils$NonClosingInputStream); line: 1, column: 224] (through reference chain: tn.esprit.pfe.approbation.entities.User["authorities"]->java.util.ArrayList[1])
        //       at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:67)
        //       at com.fasterxml.jackson.databind.DeserializationContext.reportBadDefinition(DeserializationContext.java:1915)
        //       at com.fasterxml.jackson.databind.DatabindContext.reportBadDefinition(DatabindContext.java:414)
        //       at com.fasterxml.jackson.databind.DeserializationContext.handleMissingInstantiator(DeserializationContext.java:1360)
        //       at com.fasterxml.jackson.databind.deser.AbstractDeserializer.deserialize(AbstractDeserializer.java:274)
        //       at com.fasterxml.jackson.databind.deser.std.CollectionDeserializer._deserializeFromArray(CollectionDeserializer.java:359)
        //       at com.fasterxml.jackson.databind.deser.std.CollectionDeserializer.deserialize(CollectionDeserializer.java:272)
        //       at com.fasterxml.jackson.databind.deser.std.CollectionDeserializer.deserialize(CollectionDeserializer.java:28)
        //       at com.fasterxml.jackson.databind.deser.impl.SetterlessProperty.deserializeAndSet(SetterlessProperty.java:134)
        //       at com.fasterxml.jackson.databind.deser.BeanDeserializer.deserializeFromObject(BeanDeserializer.java:392)
        //       at com.fasterxml.jackson.databind.deser.BeanDeserializer.deserialize(BeanDeserializer.java:185)
        //       at com.fasterxml.jackson.databind.deser.DefaultDeserializationContext.readRootValue(DefaultDeserializationContext.java:323)
        //       at com.fasterxml.jackson.databind.ObjectReader._bindAndClose(ObjectReader.java:2105)
        //       at com.fasterxml.jackson.databind.ObjectReader.readValue(ObjectReader.java:1481)
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:590)
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        (new UserController()).addUser(mock(User.class));
    }

    /**
     * Test {@link UserController#getTeam(User)}.
     * <p>
     * Method under test: {@link UserController#getTeam(User)}
     */
    @Test
    @DisplayName("Test getTeam(User)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"org.springframework.http.ResponseEntity UserController.getTeam(User)"})
    void testGetTeam() throws Exception {
        // Arrange
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/management/team");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Test {@link UserController#getTeamLeaves(User)}.
     * <ul>
     *   <li>Given {@link TypeConge} (default constructor) Id is one.</li>
     *   <li>Then content string a string.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserController#getTeamLeaves(User)}
     */
    @Test
    @DisplayName("Test getTeamLeaves(User); given TypeConge (default constructor) Id is one; then content string a string")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"org.springframework.http.ResponseEntity UserController.getTeamLeaves(User)"})
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
        manager.setOccurAutorisation(2);
        manager.setOnLeave(true);
        manager.setPassword("iloveyou");
        manager.setRole(Role.USER);
        manager.setSoldeAutorisation(2);
        manager.setSoldeConge(20.0d);
        manager.setTokens(new ArrayList<>());

        User manager2 = new User();
        manager2.setAvatar("Avatar");
        manager2.setEmail("jane.doe@example.org");
        manager2.setFirstName("Jane");
        manager2.setId(1);
        manager2.setLastName("Doe");
        manager2.setManager(manager);
        manager2.setMatricule("Matricule");
        manager2.setOccurAutorisation(2);
        manager2.setOnLeave(true);
        manager2.setPassword("iloveyou");
        manager2.setRole(Role.USER);
        manager2.setSoldeAutorisation(2);
        manager2.setSoldeConge(20.0d);
        manager2.setTokens(new ArrayList<>());

        User user = new User();
        user.setAvatar("Avatar");
        user.setEmail("jane.doe@example.org");
        user.setFirstName("Jane");
        user.setId(1);
        user.setLastName("Doe");
        user.setManager(manager2);
        user.setMatricule("Matricule");
        user.setOccurAutorisation(2);
        user.setOnLeave(true);
        user.setPassword("iloveyou");
        user.setRole(Role.USER);
        user.setSoldeAutorisation(2);
        user.setSoldeConge(20.0d);
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
                .andExpect(MockMvcResultMatchers.content()
                        .string("<List><item><id>1</id><requestDate>1970</requestDate><requestDate>1</requestDate><requestDate>1<"
                                + "/requestDate><requestDate>0</requestDate><requestDate>0</requestDate><startDate>1970</startDate>"
                                + "<startDate>1</startDate><startDate>1</startDate><startDate>0</startDate><startDate>0</startDate><endDate"
                                + ">1970</endDate><endDate>1</endDate><endDate>1</endDate><endDate>0</endDate><endDate>0</endDate><approved"
                                + ">true</approved><procInstId>42</procInstId><goAfterMidday>true</goAfterMidday><backAfterMidday>true<"
                                + "/backAfterMidday><type><id>1</id><name>Name</name></type><user><id>1</id><matricule>Matricule</matricule"
                                + "><firstName>Jane</firstName><lastName>Doe</lastName><email>jane.doe@example.org</email><avatar>Avatar"
                                + "</avatar><soldeConge>20.0</soldeConge><role>USER</role><soldeAutorisation>2</soldeAutorisation>"
                                + "<occurAutorisation>2</occurAutorisation><enabled>true</enabled><authorities><authorities><authority"
                                + ">ROLE_USER</authority></authorities></authorities><username>jane.doe@example.org</username><accountNonExpired"
                                + ">true</accountNonExpired><credentialsNonExpired>true</credentialsNonExpired><onLeave>true</onLeave>"
                                + "<accountNonLocked>true</accountNonLocked><manager><id>1</id><matricule>Matricule</matricule><firstName"
                                + ">Jane</firstName><lastName>Doe</lastName><email>jane.doe@example.org</email><avatar>Avatar</avatar>"
                                + "<role>USER</role><soldeAutorisation>2</soldeAutorisation><occurAutorisation>2</occurAutorisation>"
                                + "<enabled>true</enabled><authorities><authorities><authority>ROLE_USER</authority></authorities><"
                                + "/authorities><username>jane.doe@example.org</username><accountNonExpired>true</accountNonExpired>"
                                + "<credentialsNonExpired>true</credentialsNonExpired><accountNonLocked>true</accountNonLocked></manager"
                                + "></user></item></List>"));
    }

    /**
     * Test {@link UserController#getTeamLeaves(User)}.
     * <ul>
     *   <li>Then content string {@code <List/>}.</li>
     * </ul>
     * <p>
     * Method under test: {@link UserController#getTeamLeaves(User)}
     */
    @Test
    @DisplayName("Test getTeamLeaves(User); then content string '<List/>'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"org.springframework.http.ResponseEntity UserController.getTeamLeaves(User)"})
    void testGetTeamLeaves_thenContentStringList() throws Exception {
        // Arrange
        when(iGestionUser.getTeamLeaves(Mockito.<User>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/management/leaves");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }
    @Test
    @DisplayName("Test requestLeave success")
    void testRequestLeave_Success() throws Exception {
        LeaveRequestDto requestDto = new LeaveRequestDto();
        when(leaveService.handleLeaveRequest(any(LeaveRequestDto.class))).thenReturn("Leave request processed");

        String jsonRequest = "{\"someField\":\"someValue\"}"; // Adjust based on actual LeaveRequestDto structure

        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(MockMvcRequestBuilders.post("/api/v1/management/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Leave request processed"));
    }

    @Test
    @DisplayName("Test requestAuthorization success")
    void testRequestAuthorization_Success() throws Exception {
        AuthorizationRequestDto requestDto = new AuthorizationRequestDto();
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
        user.setAvatar("test-image.jpg"); // Just set the filename

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Mock the file reading behavior
        byte[] imageBytes = "test image content".getBytes();
        when(Files.readAllBytes(any(Path.class))).thenReturn(imageBytes);

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
