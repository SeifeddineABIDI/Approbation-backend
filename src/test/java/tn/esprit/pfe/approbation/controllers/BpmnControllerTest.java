package tn.esprit.pfe.approbation.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BpmnController.class)
class BpmnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Path tempDir;

    @BeforeEach
    void setup() throws Exception {
        tempDir = Files.createTempDirectory("bpmn-test");
        System.setProperty("bpmn.modeler.dir", tempDir.toString());
        Path filePath = tempDir.resolve("test-process.bpmn");
        Files.writeString(filePath, "<definitions>Test BPMN</definitions>");
    }

    @AfterEach
    void cleanup() throws Exception {
        Files.walk(tempDir)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (Exception ignored) {}
                });
        System.clearProperty("bpmn.modeler.dir");
    }

    @Test
    void shouldListBpmnFiles() throws Exception {
        mockMvc.perform(get("/api/bpmn/files"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("test-process.bpmn")));
    }

    @Test
    void shouldReturnBpmnFileContent() throws Exception {
        mockMvc.perform(get("/api/bpmn/test-process.bpmn"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<definitions>Test BPMN</definitions>")));
    }

    @Test
    void shouldReturn404ForMissingBpmnFile() throws Exception {
        mockMvc.perform(get("/api/bpmn/missing-file.bpmn"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRedeployBpmnFile() throws Exception {
        mockMvc.perform(put("/api/bpmn/deploy")
                        .param("fileName", "test-process.bpmn"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("BPMN file updated successfully")));
    }

    @Test
    void shouldReturnNotFoundWhenDeployingNonExistentFile() throws Exception {
        mockMvc.perform(put("/api/bpmn/deploy")
                        .param("fileName", "nonexistent.bpmn"))
                .andExpect(status().isNotFound());
    }
}