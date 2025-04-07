package tn.esprit.pfe.approbation.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BpmnController.class)
class BpmnControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String BPMN_DIR = "src/main/resources/static/modeler/";

    @BeforeEach
    void setup() throws Exception {
        Path folder = Paths.get(BPMN_DIR);
        Files.createDirectories(folder);
        Path filePath = folder.resolve("test-process.bpmn");
        Files.writeString(filePath, "<definitions>Test BPMN</definitions>");
    }

    @AfterEach
    void cleanup() throws Exception {
        Files.walk(Paths.get(BPMN_DIR))
                .map(Path::toFile)
                .forEach(File::delete);
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
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldRedeployBpmnFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/bpmn/deploy")
                        .param("fileName", "test-process.bpmn"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("BPMN file updated successfully")));
    }

    @Test
    void shouldReturnNotFoundWhenDeployingNonExistentFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/bpmn/deploy")
                        .param("fileName", "nonexistent.bpmn"))
                .andExpect(status().isNotFound());
    }
}
