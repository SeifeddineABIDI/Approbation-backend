package tn.esprit.pfe.approbation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApprobationApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApprobationApplication.class, args);
    }


}
