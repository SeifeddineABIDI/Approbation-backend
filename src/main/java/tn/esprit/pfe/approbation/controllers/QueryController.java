package tn.esprit.pfe.approbation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.pfe.approbation.config.QueryService;

import java.util.Map;

@RestController
@RequestMapping("/query")
@CrossOrigin(origins = "*")
public class QueryController {

    private final QueryService queryService;

    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping("/execute")
    public ResponseEntity<?> executeQuery(@RequestBody Map<String, String> queryRequest) {
        try {
            String result = queryService.executeQuery(queryRequest);
            return ResponseEntity.ok(Map.of("result", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something broke: " + e.getMessage()));
        }
    }
}