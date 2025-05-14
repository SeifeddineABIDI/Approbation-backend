package tn.esprit.pfe.approbation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {

    private final RestTemplate restTemplate;
    private final String ollamaApiUrl = "http://frontend.192.168.2.189.nip.io:11434/api/generate";
    private final String queryApiUrl = "http://frontend.192.168.2.189.nip.io:8080/query/execute";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OllamaService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String askOllama(String prompt) {
        try {
            // Step 1: Generate SQL query or detect non-data question
            String ollamaPrompt = "Your name is Bird. You’re helping with a leave management system with tables USER (id, matricule, first_name, last_name, email, solde_conge, role, manager_id) and LEAVE_REQUEST (id, request_date, start_date, end_date, approved, proc_inst_id, user_id). " +
                    "If the prompt asks for data, return a single SELECT SQL query. If it’s not about data, return 'CHAT'. " +
                    "\nPrompt: " + prompt;

            Map<String, Object> ollamaBody = new HashMap<>();
            ollamaBody.put("model", "llama3.2");
            ollamaBody.put("prompt", ollamaPrompt);
            ollamaBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> ollamaRequest = new HttpEntity<>(ollamaBody, headers);

            ResponseEntity<Map> ollamaResponse = restTemplate.postForEntity(ollamaApiUrl, ollamaRequest, Map.class);
            if (!ollamaResponse.getStatusCode().is2xxSuccessful() || ollamaResponse.getBody() == null) {
                return "Whoops, couldn’t connect. Wanna try again?";
            }

            String sqlQuery = (String) ollamaResponse.getBody().get("response");
            if (sqlQuery == null || sqlQuery.trim().isEmpty() || sqlQuery.equals("CHAT")) {
                return handleNonDataQuestion(prompt);
            }

            // Step 2: Run the query
            Map<String, String> queryRequest = Map.of("sql", sqlQuery);
            HttpEntity<Map<String, String>> queryHttpRequest = new HttpEntity<>(queryRequest, headers);

            ResponseEntity<Map> queryResponse = restTemplate.postForEntity(queryApiUrl, queryHttpRequest, Map.class);
            if (!queryResponse.getStatusCode().is2xxSuccessful() || queryResponse.getBody() == null) {
                return "The query didn’t work—maybe a glitch? Try something else!";
            }

            String queryResult = (String) queryResponse.getBody().get("result");
            if (queryResult == null) {
                return "Got nothing back from the database. Maybe it’s empty?";
            }

            // Step 3: Format the answer
            String formatPrompt = "Your name is Bird. You’re a chill chatbot. Take this JSON data and answer the user’s question like a friend. " +
                    "If they want a name, just say 'FirstName LastName'. If there’s an error or no data, keep it casual like 'Oops, nada here!' " +
                    "\nData: " + queryResult +
                    "\nQuestion: " + prompt;

            ollamaBody.put("prompt", formatPrompt);
            ollamaRequest = new HttpEntity<>(ollamaBody, headers);

            ResponseEntity<Map> formatResponse = restTemplate.postForEntity(ollamaApiUrl, ollamaRequest, Map.class);
            if (formatResponse.getStatusCode().is2xxSuccessful() && formatResponse.getBody() != null) {
                String finalResponse = (String) formatResponse.getBody().get("response");
                if (finalResponse != null && !finalResponse.trim().isEmpty()) {
                    return finalResponse;
                }
            }

            return "Found something, but it’s kinda messy: " ;
        } catch (RestClientException e) {
            System.err.println("RestClientException: " + e.getMessage());
            return "Can’t reach the server right now—check if Ollama or the backend’s up!";
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return "Something tripped up: " + e.getMessage();
        }
    }

    private String handleNonDataQuestion(String prompt) {
        String ollamaPrompt = "Your name is Bird. You’re a friendly chatbot who loves to chat about anything. " +
                "Answer the user’s prompt like you’re catching up with a buddy. Keep it short, natural, and fun. " +
                "\nPrompt: " + prompt;

        Map<String, Object> ollamaBody = new HashMap<>();
        ollamaBody.put("model", "llama3.2");
        ollamaBody.put("prompt", ollamaPrompt);
        ollamaBody.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> ollamaRequest = new HttpEntity<>(ollamaBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(ollamaApiUrl, ollamaRequest, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String reply = (String) response.getBody().get("response");
                return reply != null ? reply : "Hmm, I’m drawing a blank. What else you got?";
            }
            return "Oops, couldn’t come up with a reply. Try again?";
        } catch (RestClientException e) {
            System.err.println("RestClientException in non-data: " + e.getMessage());
            return "Server’s acting shy—let’s try that again!";
        }
    }
}