package tn.esprit.pfe.approbation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import tn.esprit.pfe.approbation.repositories.LeaveRequestRepository;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @PersistenceContext
    private EntityManager entityManager;
    public AnalyticsService(LeaveRequestRepository leaveRequestRepository, UserRepository userRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.userRepository = userRepository;
    }

    public String generateAnalyticsData(String prompt) {
        try {
            String normalizedPrompt = prompt.toLowerCase().trim();
            // Handle empty database
            if (userRepository.count() == 0) {
                return "{\"error\": \"No users in the database.\"}";
            }
            if (leaveRequestRepository.count() == 0) {
                return "{\"error\": \"No leave requests in the database.\"}";
            }
            if (normalizedPrompt.startsWith("select ")) {
                return executeSqlQuery(normalizedPrompt);
            }
            if (normalizedPrompt.contains("most leaves") ||
                    normalizedPrompt.contains("highest number of total leaves")) {
                String sql = "SELECT u.first_name, u.last_name, COUNT(lr.id) as leave_count " +
                        "FROM user u LEFT JOIN leave_request lr ON u.id = lr.user_id " +
                        "GROUP BY u.id, u.first_name, u.last_name " +
                        "ORDER BY leave_count DESC LIMIT 1";
                return executeSqlQuery(sql);
            } else if (normalizedPrompt.contains("approval rate") ||
                    normalizedPrompt.contains("how many approved")) {
                String sql = "SELECT (SUM(CASE WHEN approved = true THEN 1 ELSE 0 END) * 100.0 / COUNT(*)) as rate " +
                        "FROM leave_request";
                return executeSqlQuery(sql);
            } else if (normalizedPrompt.contains("leaves for") &&
                    (normalizedPrompt.contains("john") || normalizedPrompt.contains("jane") || normalizedPrompt.contains("bob"))) {
                String name = normalizedPrompt.contains("john") ? "John Doe" :
                        normalizedPrompt.contains("jane") ? "Jane Smith" : "Bob Johnson";
                String[] parts = name.split(" ");
                String sql = "SELECT COUNT(*) as leave_count " +
                        "FROM leave_request lr JOIN user u ON lr.user_id = u.id " +
                        "WHERE u.first_name = ?1 AND u.last_name = ?2";
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter(1, parts[0]);
                query.setParameter(2, parts[1]);
                Number count = (Number) query.getSingleResult();
                return String.format("{\"leave_count\": %d}", count.longValue());
            }
            return "{\"error\": \"I didn't understand that. Try an SQL query or ask about leaves, like 'Who has the most leaves?'\"}";
        } catch (Exception e) {
            return String.format("{\"error\": \"Something went wrong: %s\"}", e.getMessage());
        }
    }

    private String executeSqlQuery(String sql) throws Exception {
        // Validate: Only SELECT, no dangerous keywords
        String normalizedSql = sql.toLowerCase().trim();
        if (!normalizedSql.startsWith("select ") ||
                normalizedSql.contains("insert ") ||
                normalizedSql.contains("update ") ||
                normalizedSql.contains("delete ") ||
                normalizedSql.contains(";")) {
            return "{\"error\": \"Only SELECT queries are allowed, no semicolons.\"}";
        }
        if (!normalizedSql.contains("user") && !normalizedSql.contains("leave_request")) {
            return "{\"error\": \"Queries must use USER or LEAVE_REQUEST.\"}";
        }
        Query query = entityManager.createNativeQuery(sql);
        List<Object> results = query.getResultList();
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (results.isEmpty()) {
            return "{\"results\": [], \"message\": \"No data found.\"}";
        }
        for (Object result : results) {
            Map<String, Object> row = new HashMap<>();
            if (result instanceof Object[]) {
                Object[] columns = (Object[]) result;
                if (columns.length >= 2 && normalizedSql.contains("first_name")) {
                    row.put("first_name", columns[0]);
                    row.put("last_name", columns[1]);
                    if (columns.length > 2) {
                        row.put("leave_count", columns[2]);
                    }
                } else if (columns.length == 1) {
                    row.put("value", columns[0]);
                }
            } else {
                row.put("value", result);
            }
            resultList.add(row);
        }
        return objectMapper.writeValueAsString(Map.of("results", resultList));
    }
}