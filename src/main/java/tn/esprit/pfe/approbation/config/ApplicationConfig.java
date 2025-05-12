package tn.esprit.pfe.approbation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.client.RestTemplate;
import tn.esprit.pfe.approbation.entities.Role;
import tn.esprit.pfe.approbation.entities.User;
import tn.esprit.pfe.approbation.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            if (userRepository.findByEmail("hughes.brian@company.com").isEmpty()) {
                User adminUser = User.builder()
                        .firstName("Brian")
                        .lastName("Hughes")
                        .email("hughes.brian@company.com")
                        .password(passwordEncoder.encode("admin"))
                        .role(Role.ADMIN) // Assuming Role.ADMIN exists in your Role enum
                        .soldeConge(20.0) // Default from entity
                        .soldeAutorisation(2) // Default from entity
                        .occurAutorisation(2) // Default from entity
                        .isOnLeave(false) // Default from entity
                        .build();

                userRepository.save(adminUser);
                System.out.println("Admin user initialized: hughes.brian@company.com");
            } else {
                System.out.println("Admin user already exists: hughes.brian@company.com");
            }
        };
    }

}