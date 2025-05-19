package tn.esprit.pfe.approbation.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://frontend.192.168.2.189.nip.io")
@RestController
public class BpmnController {

    private static final Logger logger = LoggerFactory.getLogger(BpmnController.class);
    private static final String CAMUNDA_ENGINE_URL = System.getenv("CAMUNDA_ENGINE_URL") != null
            ? System.getenv("CAMUNDA_ENGINE_URL")
            : "http://backend-service.default.svc.cluster.local:8080/engine-rest";

    // Endpoint to list all process definitions grouped by key with versions
    @GetMapping("/api/bpmn/processes")
    public ResponseEntity<List<ProcessInfo>> getProcessDefinitions() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = CAMUNDA_ENGINE_URL + "/process-definition?latestVersion=false";

            ResponseEntity<ProcessDefinition[]> response = restTemplate.getForEntity(url, ProcessDefinition[].class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Failed to fetch processes from Camunda: {}", response.getStatusCode());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.emptyList());
            }

            // Group process definitions by key
            Map<String, List<ProcessDefinition>> groupedByKey = Arrays.stream(response.getBody())
                    .collect(Collectors.groupingBy(ProcessDefinition::getKey));

            // Create response with process info
            List<ProcessInfo> processInfos = groupedByKey.entrySet().stream()
                    .map(entry -> {
                        String key = entry.getKey();
                        List<ProcessVersion> versions = entry.getValue().stream()
                                .map(def -> new ProcessVersion(
                                        def.getId(),
                                        def.getVersion(),
                                        def.getName() != null ? def.getName() : def.getKey(),
                                        def.getResource(),
                                        def.getDeploymentId()
                                ))
                                .sorted(Comparator.comparingInt(ProcessVersion::getVersion).reversed())
                                .collect(Collectors.toList());
                        return new ProcessInfo(key, versions.get(0).getName(), versions);
                    })
                    .sorted(Comparator.comparing(ProcessInfo::getKey))
                    .collect(Collectors.toList());

            if (processInfos.isEmpty()) {
                logger.info("No processes found in Camunda");
                return ResponseEntity.ok(Collections.emptyList());
            }

            logger.info("Fetched {} processes from Camunda", processInfos.size());
            return ResponseEntity.ok(processInfos);
        } catch (Exception e) {
            logger.error("Error fetching process definitions: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    // Endpoint to get BPMN XML for a specific process definition
    @GetMapping("/api/bpmn/process/{definitionId}")
    public ResponseEntity<String> getBpmnByDefinitionId(@PathVariable String definitionId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = CAMUNDA_ENGINE_URL + "/process-definition/" + definitionId + "/xml";

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                logger.error("Failed to fetch BPMN XML for definition ID: {}", definitionId);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to fetch BPMN XML");
            }

            String bpmnXml = (String) response.getBody().get("bpmn20Xml");
            if (bpmnXml == null) {
                logger.error("No BPMN XML found for definition ID: {}", definitionId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("BPMN XML not found");
            }

            logger.info("Fetched BPMN XML for definition ID: {}", definitionId);
            return ResponseEntity.ok(bpmnXml);
        } catch (Exception e) {
            logger.error("Error fetching BPMN XML for definition ID {}: {}", definitionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching BPMN XML: " + e.getMessage());
        }
    }

    // Endpoint to redeploy a new version of a process
    @PostMapping("/api/bpmn/deploy")
    public ResponseEntity<String> deployProcess(@RequestParam("fileName") String fileName, @RequestBody String bpmnXml) {
        logger.info("Deploying new version of BPMN: {}", fileName);
        try {
            // Deploy the XML to Camunda
            deployToCamunda(fileName, bpmnXml);
            logger.info("BPMN file deployed successfully: {}", fileName);
            return ResponseEntity.ok("BPMN file deployed successfully: " + fileName);
        } catch (IOException e) {
            logger.error("Error deploying BPMN file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deploying BPMN file: " + e.getMessage());
        }
    }

    @DeleteMapping("/api/bpmn/process/{definitionId}")
    public ResponseEntity<Void> deleteProcessDefinition(@PathVariable String definitionId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = CAMUNDA_ENGINE_URL + "/process-definition/" + definitionId;

            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );

            logger.info("Camunda DELETE response for definition {}: Status {}", definitionId, response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                logger.info("Successfully deleted process definition: {}", definitionId);
                return ResponseEntity.noContent().build();
            } else {
                logger.error("Failed to delete process definition: {}, Status: {}", definitionId, response.getStatusCode());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            logger.error("Error deleting process definition {}: {}", definitionId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private void deployToCamunda(String fileName, String bpmnXml) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String deploymentUrl = CAMUNDA_ENGINE_URL + "/deployment/create";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource fileResource = new ByteArrayResource(bpmnXml.getBytes()) {
            @Override
            public String getFilename() {
                return fileName.endsWith(".bpmn") ? fileName : fileName + ".bpmn";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("deployment-name", fileName);
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                deploymentUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.CREATED) {
            throw new IOException("Failed to deploy BPMN to Camunda: " + response.getBody());
        }
        logger.info("Deployment successful: {}", response.getBody());
    }

    // DTO classes for structured response
    private static class ProcessDefinition {
        private String id;
        private String key;
        private String name;
        private String resource;
        private String deploymentId;
        private int version;

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
        public int getVersion() { return version; }
        public void setVersion(int version) { this.version = version; }
    }

    private static class ProcessInfo {
        private String key;
        private String name;
        private List<ProcessVersion> versions;

        public ProcessInfo(String key, String name, List<ProcessVersion> versions) {
            this.key = key;
            this.name = name;
            this.versions = versions;
        }

        public String getKey() { return key; }
        public String getName() { return name; }
        public List<ProcessVersion> getVersions() { return versions; }
    }

    private static class ProcessVersion {
        private String id;
        private int version;
        private String name;
        private String resource;
        private String deploymentId;

        public ProcessVersion(String id, int version, String name, String resource, String deploymentId) {
            this.id = id;
            this.version = version;
            this.name = name;
            this.resource = resource;
            this.deploymentId = deploymentId;
        }

        public String getId() { return id; }
        public int getVersion() { return version; }
        public String getName() { return name; }
        public String getResource() { return resource; }
        public String getDeploymentId() { return deploymentId; }
    }
}