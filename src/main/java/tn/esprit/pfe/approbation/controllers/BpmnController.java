    package tn.esprit.pfe.approbation.controllers;


    import org.springframework.core.io.Resource;
    import org.springframework.core.io.ClassPathResource;
    import org.springframework.web.bind.annotation.*;

    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;

    @CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    @RestController
    public class BpmnController {

        @GetMapping("/api/bpmn/{fileName}")
        public String getBpmnFile(@PathVariable String fileName) throws IOException {
            Resource resource = new ClassPathResource("static/modeler/" + fileName);
            if (!resource.exists()) {
                throw new IOException("BPMN file not found: " + fileName);
            }
            Path path = resource.getFile().toPath();
            return new String(Files.readAllBytes(path));
        }
    }