package tn.esprit.pfe.approbation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pfe.approbation.config.OllamaService;

import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://frontend.192.168.2.189.nip.io")
public class ChatController {

    private final OllamaService ollamaService;

    public ChatController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/natural")
    public ResponseEntity<?> chatWithOllama(@RequestBody Map<String, String> body) {
        try {
            String prompt = body.get("prompt");
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Say something, I’m all ears!"));
            }

            String response = ollamaService.askOllama(prompt);
            return ResponseEntity.ok(Map.of("response", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Yikes, something broke: " + e.getMessage()));
        }
    }
}