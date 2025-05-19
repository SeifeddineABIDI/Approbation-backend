package tn.esprit.pfe.approbation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://frontend.192.168.2.189.nip.io")
@RestController
public class BpmnController {

    private static final String CAMUNDA_ENGINE_URL = System.getenv("CAMUNDA_ENGINE_URL") != null
            ? System.getenv("CAMUNDA_ENGINE_URL")
            : "http://backend-service.default.svc.cluster.local:8080/engine-rest";    private static final String BPMN_DIR = "static/modeler/";

    @GetMapping("/api/bpmn/files")
    public ResponseEntity<List<String>> getBpmnFiles() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = CAMUNDA_ENGINE_URL + "/process-definition?latestVersion=true";

            ResponseEntity<ProcessDefinition[]> response = restTemplate.getForEntity(url, ProcessDefinition[].class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(List.of("Failed to fetch processes from Camunda"));
            }

            List<String> processNames = Arrays.stream(response.getBody())
                    .map(def -> def.getName() != null ? def.getName() : def.getKey())
                    .collect(Collectors.toList());

            if (processNames.isEmpty()) {
                return ResponseEntity.ok(List.of("No processes found"));
            }
            return ResponseEntity.ok(processNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of("Error fetching processes: " + e.getMessage()));
        }
    }
    private static class ProcessDefinition {
        private String id;
        private String key;
        private String name;
        private String resource;
        private String deploymentId;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        public String getDeploymentId() { return deploymentId; }
        public void setDeploymentId(String deploymentId) { this.deploymentId = deploymentId; }
    }
    @GetMapping("/api/bpmn/{fileName}")
    public String getBpmnFile(@PathVariable String fileName) throws IOException {
        Logger logger = LoggerFactory.getLogger(BpmnController.class);
        logger.info("Requesting BPMN file: {}", fileName);

        // Try with and without .bpmn extension
        String[] possibleFileNames = {fileName, fileName + ".bpmn"};
        Resource resource = null;
        String resolvedFileName = null;

        for (String fname : possibleFileNames) {
            resource = new ClassPathResource("static/modeler/" + fname);
            if (resource.exists()) {
                resolvedFileName = fname;
                break;
            }
        }

        if (resource == null || !resource.exists()) {
            logger.error("BPMN file not found for: {}", fileName);
            throw new IOException("BPMN file not found: " + fileName);
        }

        logger.info("Found BPMN file: static/modeler/{}", resolvedFileName);
        return new String(resource.getInputStream().readAllBytes());
    }
    @PutMapping("/api/bpmn/deploy")
    public ResponseEntity<String> updateDeployment(@RequestParam("fileName") String fileName) {
        Logger logger = LoggerFactory.getLogger(BpmnController.class);
        logger.info("Deploying BPMN file: {}", fileName);
        try {
            String actualFileName = fileName.endsWith(".bpmn") ? fileName : fileName + ".bpmn";
            Resource resource = new ClassPathResource("static/modeler/" + actualFileName);
            if (!resource.exists()) {
                logger.error("BPMN file not found: {}", actualFileName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("BPMN file not found: " + fileName);
            }

            // Rest of the method remains the same
            String deploymentId = getDeploymentId(fileName);
            if (deploymentId != null) {
                deleteDeployment(deploymentId);
            }
            deployToCamunda(resource);
            return ResponseEntity.ok("BPMN file updated successfully: " + fileName);
        } catch (IOException e) {
            logger.error("Error updating BPMN file: {}", e.getMessage(), e);
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
