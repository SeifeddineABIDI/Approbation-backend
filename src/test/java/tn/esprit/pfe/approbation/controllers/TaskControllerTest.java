package tn.esprit.pfe.approbation.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.MethodsUnderTest;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.pfe.approbation.dtos.TaskConfirmationDTO;
import tn.esprit.pfe.approbation.dtos.TaskDTO;
import tn.esprit.pfe.approbation.dtos.TaskDetailsDto;
import tn.esprit.pfe.approbation.entities.LeaveRequest;
import tn.esprit.pfe.approbation.entities.Role;
import tn.esprit.pfe.approbation.entities.TypeConge;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import tn.esprit.pfe.approbation.services.LeaveService;
import tn.esprit.pfe.approbation.services.ReportService;

@ContextConfiguration(classes = {TaskController.class})
@ExtendWith(SpringExtension.class)
class TaskControllerTest {

    @MockBean
    private LeaveService leaveService;

    @Autowired
    private TaskController taskController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private LeaveRequestRepository leaveRequestRepository;

    @MockBean
    private ReportService reportService;

    /**
     * Test {@link TaskController#getTasksByUser(String)}.
     * <ul>
     *   <li>When {@code 42}.</li>
     *   <li>Then return StatusCodeValue is five hundred.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getTasksByUser(String)}
     */
    @Test
    @DisplayName("Test getTasksByUser(String); when '42'; then return StatusCodeValue is five hundred")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"ResponseEntity TaskController.getTasksByUser(String)"})
    void testGetTasksByUser_when42_thenReturnStatusCodeValueIsFiveHundred() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange and Act
        ResponseEntity<List<TaskDTO>> actualTasksByUser = (new TaskController()).getTasksByUser("42");

        // Assert
        HttpStatusCode statusCode = actualTasksByUser.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertNull(actualTasksByUser.getBody());
        assertEquals(500, actualTasksByUser.getStatusCodeValue());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
        assertFalse(actualTasksByUser.hasBody());
        assertTrue(actualTasksByUser.getHeaders().isEmpty());
    }

    /**
     * Test {@link TaskController#getTasksByUser(String)}.
     * <ul>
     *   <li>When empty string.</li>
     *   <li>Then return StatusCodeValue is four hundred.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getTasksByUser(String)}
     */
    @Test
    @DisplayName("Test getTasksByUser(String); when empty string; then return StatusCodeValue is four hundred")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"ResponseEntity TaskController.getTasksByUser(String)"})
    void testGetTasksByUser_whenEmptyString_thenReturnStatusCodeValueIsFourHundred() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange and Act
        ResponseEntity<List<TaskDTO>> actualTasksByUser = (new TaskController()).getTasksByUser("");

        // Assert
        HttpStatusCode statusCode = actualTasksByUser.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertNull(actualTasksByUser.getBody());
        assertEquals(400, actualTasksByUser.getStatusCodeValue());
        assertEquals(HttpStatus.BAD_REQUEST, statusCode);
        assertFalse(actualTasksByUser.hasBody());
        assertTrue(actualTasksByUser.getHeaders().isEmpty());
    }

    /**
     * Test {@link TaskController#getTasksByUser(String)}.
     * <ul>
     *   <li>When {@code null}.</li>
     *   <li>Then return StatusCodeValue is four hundred.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getTasksByUser(String)}
     */
    @Test
    @DisplayName("Test getTasksByUser(String); when 'null'; then return StatusCodeValue is four hundred")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"ResponseEntity TaskController.getTasksByUser(String)"})
    void testGetTasksByUser_whenNull_thenReturnStatusCodeValueIsFourHundred() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange and Act
        ResponseEntity<List<TaskDTO>> actualTasksByUser = (new TaskController()).getTasksByUser(null);

        // Assert
        HttpStatusCode statusCode = actualTasksByUser.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertNull(actualTasksByUser.getBody());
        assertEquals(400, actualTasksByUser.getStatusCodeValue());
        assertEquals(HttpStatus.BAD_REQUEST, statusCode);
        assertFalse(actualTasksByUser.hasBody());
        assertTrue(actualTasksByUser.getHeaders().isEmpty());
    }

    /**
     * Test {@link TaskController#confirmManagerTask1(UUID, TaskConfirmationDTO)}.
     * <p>
     * Method under test: {@link TaskController#confirmManagerTask1(UUID, TaskConfirmationDTO)}
     */
    @Test
    @DisplayName("Test confirmManagerTask1(UUID, TaskConfirmationDTO)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"ResponseEntity TaskController.confirmManagerTask1(UUID, TaskConfirmationDTO)"})
    void testConfirmManagerTask1() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        TaskController taskController = new TaskController();
        UUID taskId = UUID.randomUUID();

        TaskConfirmationDTO confirmationDto = new TaskConfirmationDTO();
        confirmationDto.setApprovalStatus("Approval Status");
        confirmationDto.setComments("Comments");

        // Act
        ResponseEntity<String> actualConfirmManagerTask1Result = taskController.confirmManagerTask1(taskId,
                confirmationDto);

        // Assert
        HttpStatusCode statusCode = actualConfirmManagerTask1Result.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertEquals(
                "Error completing task: Cannot invoke \"tn.esprit.pfe.approbation.services.LeaveService.confirmManagerTask1"
                        + "(java.util.UUID, String, String)\" because \"this.leaveService\" is null",
                actualConfirmManagerTask1Result.getBody());
        assertEquals(500, actualConfirmManagerTask1Result.getStatusCodeValue());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
        assertTrue(actualConfirmManagerTask1Result.hasBody());
        assertTrue(actualConfirmManagerTask1Result.getHeaders().isEmpty());
    }

    /**
     * Test {@link TaskController#confirmRhTask1(UUID, String, TaskConfirmationDTO)}.
     * <p>
     * Method under test: {@link TaskController#confirmRhTask1(UUID, String, TaskConfirmationDTO)}
     */
    @Test
    @DisplayName("Test confirmRhTask1(UUID, String, TaskConfirmationDTO)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"ResponseEntity TaskController.confirmRhTask1(UUID, String, TaskConfirmationDTO)"})
    void testConfirmRhTask1() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        TaskController taskController = new TaskController();
        UUID taskId = UUID.randomUUID();

        TaskConfirmationDTO confirmationDto = new TaskConfirmationDTO();
        confirmationDto.setApprovalStatus("Approval Status");
        confirmationDto.setComments("Comments");

        // Act
        ResponseEntity<String> actualConfirmRhTask1Result = taskController.confirmRhTask1(taskId, "Matricule",
                confirmationDto);

        // Assert
        HttpStatusCode statusCode = actualConfirmRhTask1Result.getStatusCode();
        assertTrue(statusCode instanceof HttpStatus);
        assertEquals(
                "Error completing task: Cannot invoke \"tn.esprit.pfe.approbation.services.LeaveService.confirmRhTask1"
                        + "(java.util.UUID, String, String, String)\" because \"this.leaveService\" is null",
                actualConfirmRhTask1Result.getBody());
        assertEquals(500, actualConfirmRhTask1Result.getStatusCodeValue());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, statusCode);
        assertTrue(actualConfirmRhTask1Result.hasBody());
        assertTrue(actualConfirmRhTask1Result.getHeaders().isEmpty());
    }

    /**
     * Test {@link TaskController#getUserByMatricule(String)}.
     * <p>
     * Method under test: {@link TaskController#getUserByMatricule(String)}
     */
    @Test
    @DisplayName("Test getUserByMatricule(String)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"tn.esprit.pfe.approbation.dtos.UserDto TaskController.getUserByMatricule(String)"})
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
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/getUserByMat/{matricule}",
                "Matricule");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
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
     * Test {@link TaskController#getUserByMatricule(String)}.
     * <ul>
     *   <li>Given {@link User#User()} Avatar is {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getUserByMatricule(String)}
     */
    @Test
    @DisplayName("Test getUserByMatricule(String); given User() Avatar is 'null'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"tn.esprit.pfe.approbation.dtos.UserDto TaskController.getUserByMatricule(String)"})
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
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/getUserByMat/{matricule}",
                "Matricule");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
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
     * Test {@link TaskController#getUserTaskStats(String)}.
     * <p>
     * Method under test: {@link TaskController#getUserTaskStats(String)}
     */
    @Test
    @DisplayName("Test getUserTaskStats(String)")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"tn.esprit.pfe.approbation.dtos.TaskStatsDto TaskController.getUserTaskStats(String)"})
    void testGetUserTaskStats() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   jakarta.servlet.ServletException: Request processing failed: java.lang.ClassCastException: class java.lang.String cannot be cast to class java.util.List (java.lang.String and java.util.List are in module java.base of loader 'bootstrap')
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564)
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
        //   java.lang.ClassCastException: class java.lang.String cannot be cast to class java.util.List (java.lang.String and java.util.List are in module java.base of loader 'bootstrap')
        //       at org.camunda.bpm.engine.impl.AbstractQuery.list(AbstractQuery.java:142)
        //       at tn.esprit.pfe.approbation.controllers.TaskController.getUserTaskStats(TaskController.java:170)
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:564)
        //       at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:658)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        (new TaskController()).getUserTaskStats("42");
    }

    /**
     * Test {@link TaskController#getTasksByProcessInstance(String)}.
     * <p>
     * Method under test: {@link TaskController#getTasksByProcessInstance(String)}
     */
    @Test
    @DisplayName("Test getTasksByProcessInstance(String)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"List TaskController.getTasksByProcessInstance(String)"})
    void testGetTasksByProcessInstance() throws Exception {
        // Arrange
        when(leaveService.getProcessTasksByInstanceId(Mockito.<String>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/process/{instanceId}", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }

    /**
     * Test {@link TaskController#getTasksByAssignee(String)}.
     * <ul>
     *   <li>Then status {@link StatusResultMatchers#isNoContent()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getTasksByAssignee(String)}
     */
    @Test
    @DisplayName("Test getTasksByAssignee(String); then status isNoContent()")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"ResponseEntity TaskController.getTasksByAssignee(String)"})
    void testGetTasksByAssignee_thenStatusIsNoContent() throws Exception {
        // Arrange
        when(leaveService.getTasksByAssignee(Mockito.<String>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/get/assignee/{assignee}",
                "Assignee");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    /**
     * Test {@link TaskController#getTasksByAssignee(String)}.
     * <ul>
     *   <li>Then status {@link StatusResultMatchers#isOk()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getTasksByAssignee(String)}
     */
    @Test
    @DisplayName("Test getTasksByAssignee(String); then status isOk()")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"ResponseEntity TaskController.getTasksByAssignee(String)"})
    void testGetTasksByAssignee_thenStatusIsOk() throws Exception {
        // Arrange
        ArrayList<TaskDetailsDto> taskDetailsDtoList = new ArrayList<>();
        Date startTime = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        taskDetailsDtoList.add(new TaskDetailsDto("42", "42", "Name", "Assignee", "Owner", startTime,
                Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()),
                "The characteristics of someone or something", "Just cause", true));
        when(leaveService.getTasksByAssignee(Mockito.<String>any())).thenReturn(taskDetailsDtoList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/get/assignee/{assignee}",
                "Assignee");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "<List><item><id>42</id><procInstId>42</procInstId><name>Name</name><assignee>Assignee</assignee><owner"
                                        + ">Owner</owner><startTime>0</startTime><endTime>0</endTime><description>The characteristics of someone"
                                        + " or something</description><deleteReason>Just cause</deleteReason><leaveApproved>true</leaveApproved"
                                        + "></item></List>"));
    }

    /**
     * Test {@link TaskController#getLeaveRequestsByMatricule(String)}.
     * <ul>
     *   <li>Given {@code /tasks/requests/{matricule}}.</li>
     *   <li>Then status {@link StatusResultMatchers#isNotFound()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getLeaveRequestsByMatricule(String)}
     */
    @Test
    @DisplayName("Test getLeaveRequestsByMatricule(String); given '/tasks/requests/{matricule}'; then status isNotFound()")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"List TaskController.getLeaveRequestsByMatricule(String)"})
    void testGetLeaveRequestsByMatricule_givenTasksRequestsMatricule_thenStatusIsNotFound() throws Exception {
        // Arrange
        when(leaveRequestRepository.findByUserMatriculeOrderByIdDesc(Mockito.<String>any())).thenReturn(new ArrayList<>());
        FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    /**
     * Test {@link TaskController#getLeaveRequestsByMatricule(String)}.
     * <ul>
     *   <li>Given {@link TypeConge} (default constructor) Id is one.</li>
     *   <li>Then content string a string.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getLeaveRequestsByMatricule(String)}
     */
    @Test
    @DisplayName("Test getLeaveRequestsByMatricule(String); given TypeConge (default constructor) Id is one; then content string a string")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"List TaskController.getLeaveRequestsByMatricule(String)"})
    void testGetLeaveRequestsByMatricule_givenTypeCongeIdIsOne_thenContentStringAString() throws Exception {
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
        when(leaveRequestRepository.findByUserMatriculeOrderByIdDesc(Mockito.<String>any())).thenReturn(leaveRequestList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/requests/{matricule}",
                "Matricule");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
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
                                + ">ROLE_USER</authority></authorities></authorities><username>jane.doe@example.org</username>"
                                + "<credentialsNonExpired>true</credentialsNonExpired><accountNonExpired>true</accountNonExpired><onLeave"
                                + ">true</onLeave><accountNonLocked>true</accountNonLocked><manager><id>1</id><matricule>Matricule<"
                                + "/matricule><firstName>Jane</firstName><lastName>Doe</lastName><email>jane.doe@example.org</email>"
                                + "<avatar>Avatar</avatar><role>USER</role><soldeAutorisation>1</soldeAutorisation><occurAutorisation>1"
                                + "</occurAutorisation><enabled>true</enabled><authorities><authorities><authority>ROLE_USER</authority"
                                + "></authorities></authorities><username>jane.doe@example.org</username><credentialsNonExpired>true<"
                                + "/credentialsNonExpired><accountNonExpired>true</accountNonExpired><accountNonLocked>true</accountNonLocked"
                                + "></manager></user></item></List>"));
    }

    /**
     * Test {@link TaskController#getLeaveRequestsByMatricule(String)}.
     * <ul>
     *   <li>When {@code Matricule}.</li>
     *   <li>Then content string {@code <List/>}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getLeaveRequestsByMatricule(String)}
     */
    @Test
    @DisplayName("Test getLeaveRequestsByMatricule(String); when 'Matricule'; then content string '<List/>'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"List TaskController.getLeaveRequestsByMatricule(String)"})
    void testGetLeaveRequestsByMatricule_whenMatricule_thenContentStringList() throws Exception {
        // Arrange
        when(leaveRequestRepository.findByUserMatriculeOrderByIdDesc(Mockito.<String>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/requests/{matricule}",
                "Matricule");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }

    /**
     * Test {@link TaskController#getLeaveRequestsConfirmedByMatricule(String)}.
     * <ul>
     *   <li>Then content string a string.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getLeaveRequestsConfirmedByMatricule(String)}
     */
    @Test
    @DisplayName("Test getLeaveRequestsConfirmedByMatricule(String); then content string a string")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"List TaskController.getLeaveRequestsConfirmedByMatricule(String)"})
    void testGetLeaveRequestsConfirmedByMatricule_thenContentStringAString() throws Exception {
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
        when(leaveRequestRepository.findByUserMatriculeAndApprovedOrderByIdDesc(Mockito.<String>any(),
                Mockito.<Boolean>any())).thenReturn(leaveRequestList);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/requestsConfirmed/{matricule}",
                "Matricule");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
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
                                + ">ROLE_USER</authority></authorities></authorities><username>jane.doe@example.org</username>"
                                + "<credentialsNonExpired>true</credentialsNonExpired><accountNonExpired>true</accountNonExpired><onLeave"
                                + ">true</onLeave><accountNonLocked>true</accountNonLocked><manager><id>1</id><matricule>Matricule<"
                                + "/matricule><firstName>Jane</firstName><lastName>Doe</lastName><email>jane.doe@example.org</email>"
                                + "<avatar>Avatar</avatar><role>USER</role><soldeAutorisation>1</soldeAutorisation><occurAutorisation>1"
                                + "</occurAutorisation><enabled>true</enabled><authorities><authorities><authority>ROLE_USER</authority"
                                + "></authorities></authorities><username>jane.doe@example.org</username><credentialsNonExpired>true<"
                                + "/credentialsNonExpired><accountNonExpired>true</accountNonExpired><accountNonLocked>true</accountNonLocked"
                                + "></manager></user></item></List>"));
    }

    /**
     * Test {@link TaskController#getLeaveRequestsConfirmedByMatricule(String)}.
     * <ul>
     *   <li>Then content string {@code <List/>}.</li>
     * </ul>
     * <p>
     * Method under test: {@link TaskController#getLeaveRequestsConfirmedByMatricule(String)}
     */
    @Test
    @DisplayName("Test getLeaveRequestsConfirmedByMatricule(String); then content string '<List/>'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"List TaskController.getLeaveRequestsConfirmedByMatricule(String)"})
    void testGetLeaveRequestsConfirmedByMatricule_thenContentStringList() throws Exception {
        // Arrange
        when(leaveRequestRepository.findByUserMatriculeAndApprovedOrderByIdDesc(Mockito.<String>any(),
                Mockito.<Boolean>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/requestsConfirmed/{matricule}",
                "Matricule");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }

    /**
     * Test {@link TaskController#generateAvisCongeReport(String, HttpServletResponse)}.
     * <p>
     * Method under test: {@link TaskController#generateAvisCongeReport(String, HttpServletResponse)}
     */
    @Test
    @DisplayName("Test generateAvisCongeReport(String, HttpServletResponse)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"void TaskController.generateAvisCongeReport(String, HttpServletResponse)"})
    void testGenerateAvisCongeReport() throws Exception {
        // Arrange
        when(reportService.generateAvisCongeReport(Mockito.<String>any())).thenReturn("AXAXAXAX".getBytes("UTF-8"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/tasks/generateAvisCongeReport")
                .param("instanceId", "foo");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(taskController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/pdf"))
                .andExpect(MockMvcResultMatchers.content().string("AXAXAXAX"));
    }
}
