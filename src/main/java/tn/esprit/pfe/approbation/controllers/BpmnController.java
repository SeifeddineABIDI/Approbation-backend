package tn.esprit.pfe.approbation.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
public class BpmnController {

    private static final String CAMUNDA_ENGINE_URL = "http://localhost:8080/engine-rest";
    private static final String BPMN_DIR = "src/main/resources/static/modeler/";

    @GetMapping("/api/bpmn/files")
    public ResponseEntity<List<String>> getBpmnFiles() {
        File folder = new File(BPMN_DIR);
        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.badRequest().body(List.of("Directory not found"));
        }
        List<String> files = Arrays.stream(folder.list((dir, name) -> name.endsWith(".bpmn")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(files);
    }

    @GetMapping("/api/bpmn/{fileName}")
    public String getBpmnFile(@PathVariable String fileName) throws IOException {
        Resource resource = new ClassPathResource("static/modeler/" + fileName);
        if (!resource.exists()) {
            throw new IOException("BPMN file not found: " + fileName);
        }
        return new String(resource.getInputStream().readAllBytes());
    }

    @PutMapping("/api/bpmn/deploy")
    public ResponseEntity<String> updateDeployment(@RequestParam("fileName") String fileName) {
        try {
            Resource resource = new ClassPathResource("static/modeler/" + fileName);
            if (!resource.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("BPMN file not found: " + fileName);
            }

            // Step 1: Find existing deployment
            String deploymentId = getDeploymentId(fileName);
            if (deploymentId != null) {
                // Step 2: Delete existing deployment
                deleteDeployment(deploymentId);
            }

            // Step 3: Deploy new version of the process
            deployToCamunda(resource);

            return ResponseEntity.ok("BPMN file updated successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating BPMN file: " + e.getMessage());
        }
    }

    private String getDeploymentId(String fileName) {
        RestTemplate restTemplate = new RestTemplate();
        String url = CAMUNDA_ENGINE_URL + "/deployment?name=" + fileName;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            // Extract deploymentId (assuming JSON response contains an array of deployments)
            String jsonResponse = response.getBody();
            if (jsonResponse.contains("\"id\":\"")) {
                return jsonResponse.split("\"id\":\"")[1].split("\"")[0]; // Extract first deployment ID
            }
        }
        return null;
    }

    private void deleteDeployment(String deploymentId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = CAMUNDA_ENGINE_URL + "/deployment/" + deploymentId + "?cascade=true";
        restTemplate.delete(url);
        System.out.println("Deleted existing deployment: " + deploymentId);
    }

    private void deployToCamunda(Resource resource) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String deploymentUrl = CAMUNDA_ENGINE_URL + "/deployment/create";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileResource = new ByteArrayResource(resource.getInputStream().readAllBytes()) {
            @Override
            public String getFilename() {
                return resource.getFilename();
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("deployment-name", resource.getFilename());
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                deploymentUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            System.out.println("Deployment successful: " + response.getBody());
        } else {
            throw new IOException("Failed to deploy BPMN to Camunda: " + response.getBody());
        }
    }
}
