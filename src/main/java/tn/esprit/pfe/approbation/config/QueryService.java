package tn.esprit.pfe.approbation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String executeQuery(Map<String, String> queryRequest) {
        try {
            String sql = queryRequest.get("sql");
            if (sql == null || sql.trim().isEmpty()) {
                return "{\"error\": \"No query to run, buddy!\"}";
            }

            String normalizedSql = sql.toLowerCase().trim();
            if (!normalizedSql.startsWith("select ") ||
                    normalizedSql.contains("insert ") ||
                    normalizedSql.contains("update ") ||
                    normalizedSql.contains("delete ") ||
                    normalizedSql.contains(";")) {
                return "{\"error\": \"Gotta stick to SELECT queries, no semicolons!\"}";
            }
            if (!normalizedSql.contains("user") && !normalizedSql.contains("leave_request")) {
                return "{\"error\": \"Only USER or LEAVE_REQUEST tables, please!\"}";
            }

            Query query = entityManager.createNativeQuery(sql);
            List<?> results = query.getResultList();
            List<Map<String, Object>> resultList = new ArrayList<>();

            // Use generic column names for native queries
            List<String> columnNames = new ArrayList<>(); // Will use col_0, col_1, etc.

            if (results.isEmpty()) {
                return "{\"results\": [], \"message\": \"Nada found.\"}";
            }

            for (Object result : results) {
                Map<String, Object> row = new HashMap<>();
                if (result instanceof Object[] arr) {
                    for (int i = 0; i < arr.length; i++) {
                        String colName = "col_" + i;
                        row.put(colName, arr[i]);
                    }
                } else {
                    row.put(columnNames.isEmpty() ? "value" : columnNames.get(0), result);
                }
                resultList.add(row);
            }

            return objectMapper.writeValueAsString(Map.of("results", resultList));
        } catch (Exception e) {
            return String.format("{\"error\": \"Query didn't fly: %s\"}", e.getMessage());
        }
    }
}