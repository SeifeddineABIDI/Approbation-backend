package tn.esprit.pfe.approbation.controllers;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.pfe.approbation.entities.Notification;
import tn.esprit.pfe.approbation.services.NotificationService;

@ContextConfiguration(classes = {NotificationController.class})
@ExtendWith(SpringExtension.class)
class NotificationControllerTest {
    @Autowired
    private NotificationController notificationController;

    @MockBean
    private NotificationService notificationService;

    /**
     * Test {@link NotificationController#getAll()}.
     * <p>
     * Method under test: {@link NotificationController#getAll()}
     */
    @Test
    @DisplayName("Test getAll()")
    @Tag("MaintainedBySeifeddineABIDI")
    @MethodsUnderTest({"java.util.List NotificationController.getAll()"})
    void testGetAll() throws Exception {
        // Arrange
        when(notificationService.getAllNotifications()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/common/notifications");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(notificationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }

    /**
     * Test {@link NotificationController#getNotificationsByUserId(String)}.
     * <p>
     * Method under test: {@link NotificationController#getNotificationsByUserId(String)}
     */
    @Test
    @DisplayName("Test getNotificationsByUserId(String)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"java.util.List NotificationController.getNotificationsByUserId(String)"})
    void testGetNotificationsByUserId() throws Exception {
        // Arrange
        when(notificationService.getNotificationsByUserId(Mockito.<String>any())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/common/notifications/user/{userId}",
                "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(notificationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("<List/>"));
    }

    /**
     * Test {@link NotificationController#create(Notification)}.
     * <p>
     * Method under test: {@link NotificationController#create(Notification)}
     */
    @Test
    @DisplayName("Test create(Notification)")
    @Tag("MaintainedBySeifeddineABIDI")
    @MethodsUnderTest({"Notification NotificationController.create(Notification)"})
    void testCreate() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setDescription("The characteristics of someone or something");
        notification.setIcon("Icon");
        notification.setId(1L);
        notification.setImage("Image");
        notification.setLink("Link");
        notification.setRead(true);
        notification.setTime("Time");
        notification.setTitle("Dr");
        notification.setUseRouter(true);
        notification.setUserId("42");
        when(notificationService.createNotification(Mockito.<Notification>any())).thenReturn(notification);

        Notification notification2 = new Notification();
        notification2.setDescription("The characteristics of someone or something");
        notification2.setIcon("Icon");
        notification2.setId(1L);
        notification2.setImage("Image");
        notification2.setLink("Link");
        notification2.setRead(true);
        notification2.setTime("Time");
        notification2.setTitle("Dr");
        notification2.setUseRouter(true);
        notification2.setUserId("42");
        String content = (new ObjectMapper()).writeValueAsString(notification2);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/common/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(notificationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("<Notification><id>1</id><userId>42</userId><icon>Icon</icon><image>Image</image><title>Dr</title>"
                                + "<description>The characteristics of someone or something</description><time>Time</time><link>Link<"
                                + "/link><useRouter>true</useRouter><read>true</read></Notification>"));
    }

    /**
     * Test {@link NotificationController#update(Long, Notification)}.
     * <p>
     * Method under test: {@link NotificationController#update(Long, Notification)}
     */
    @Test
    @DisplayName("Test update(Long, Notification)")
    @Tag("MaintainedBySeifeddineABIDI")
    @MethodsUnderTest({"Notification NotificationController.update(Long, Notification)"})
    void testUpdate() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setDescription("The characteristics of someone or something");
        notification.setIcon("Icon");
        notification.setId(1L);
        notification.setImage("Image");
        notification.setLink("Link");
        notification.setRead(true);
        notification.setTime("Time");
        notification.setTitle("Dr");
        notification.setUseRouter(true);
        notification.setUserId("42");
        when(notificationService.updateNotification(Mockito.<Long>any(), Mockito.<Notification>any()))
                .thenReturn(notification);

        Notification notification2 = new Notification();
        notification2.setDescription("The characteristics of someone or something");
        notification2.setIcon("Icon");
        notification2.setId(1L);
        notification2.setImage("Image");
        notification2.setLink("Link");
        notification2.setRead(true);
        notification2.setTime("Time");
        notification2.setTitle("Dr");
        notification2.setUseRouter(true);
        notification2.setUserId("42");
        String content = (new ObjectMapper()).writeValueAsString(notification2);
        MockHttpServletRequestBuilder patchResult = MockMvcRequestBuilders.patch("/api/common/notifications");
        MockHttpServletRequestBuilder requestBuilder = patchResult.param("id", String.valueOf(1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(notificationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("<Notification><id>1</id><userId>42</userId><icon>Icon</icon><image>Image</image><title>Dr</title>"
                                + "<description>The characteristics of someone or something</description><time>Time</time><link>Link<"
                                + "/link><useRouter>true</useRouter><read>true</read></Notification>"));
    }

    /**
     * Test {@link NotificationController#delete(Long)}.
     * <p>
     * Method under test: {@link NotificationController#delete(Long)}
     */
    @Test
    @DisplayName("Test delete(Long)")
    @Tag("MaintainedBySeifeddineABIDI")
    @MethodsUnderTest({"org.springframework.http.ResponseEntity NotificationController.delete(Long)"})
    void testDelete() throws Exception {
        // Arrange
        doNothing().when(notificationService).deleteNotification(Mockito.<Long>any());
        MockHttpServletRequestBuilder deleteResult = MockMvcRequestBuilders.delete("/api/common/notifications");
        MockHttpServletRequestBuilder requestBuilder = deleteResult.param("id", String.valueOf(1L));

        // Act and Assert
        MockMvcBuilders.standaloneSetup(notificationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Test {@link NotificationController#markAllAsRead()}.
     * <p>
     * Method under test: {@link NotificationController#markAllAsRead()}
     */
    @Test
    @DisplayName("Test markAllAsRead()")
    @Tag("MaintainedBySeifeddineABIDI")
    @MethodsUnderTest({"org.springframework.http.ResponseEntity NotificationController.markAllAsRead()"})
    void testMarkAllAsRead() throws Exception {
        // Arrange
        doNothing().when(notificationService).markAllAsRead();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/common/notifications/mark-all-as-read");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(notificationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Test {@link NotificationController#markAsRead(Long)}.
     * <p>
     * Method under test: {@link NotificationController#markAsRead(Long)}
     */
    @Test
    @DisplayName("Test markAsRead(Long)")
    @Tag("MaintainedBySeifeddineABIDI")
    @MethodsUnderTest({"Notification NotificationController.markAsRead(Long)"})
    void testMarkAsRead() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setDescription("The characteristics of someone or something");
        notification.setIcon("Icon");
        notification.setId(1L);
        notification.setImage("Image");
        notification.setLink("Link");
        notification.setRead(true);
        notification.setTime("Time");
        notification.setTitle("Dr");
        notification.setUseRouter(true);
        notification.setUserId("42");
        when(notificationService.markAsRead(Mockito.<Long>any())).thenReturn(notification);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/common/notifications/mark-as-read/{id}", 1L);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(notificationController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/xml;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("<Notification><id>1</id><userId>42</userId><icon>Icon</icon><image>Image</image><title>Dr</title>"
                                + "<description>The characteristics of someone or something</description><time>Time</time><link>Link<"
                                + "/link><useRouter>true</useRouter><read>true</read></Notification>"));
    }
}
