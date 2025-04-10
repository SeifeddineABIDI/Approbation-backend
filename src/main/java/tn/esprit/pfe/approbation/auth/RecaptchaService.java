package tn.esprit.pfe.approbation.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public RecaptchaService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public boolean verifyRecaptcha(String token) {
        String url = "https://www.google.com/recaptcha/api/siteverify";
        String params = "?secret=" + recaptchaSecret + "&response=" + token;
        try {
            String response = restTemplate.postForObject(url + params, null, String.class);
            System.out.println("reCAPTCHA response: " + response);
            JsonNode jsonNode = objectMapper.readTree(response);
            boolean success = jsonNode.get("success").asBoolean();
            double score = jsonNode.get("score").asDouble();
            System.out.println("Success: " + success + ", Score: " + score);
            return success && score >= 0.5;
        } catch (Exception e) {
            System.err.println("reCAPTCHA verification failed: " + e.getMessage());
            return false;
        }
    }
}