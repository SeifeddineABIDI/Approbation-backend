package tn.esprit.pfe.approbation.entities;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import tn.esprit.pfe.approbation.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    public static Specification<User> searchUsers(
            String firstName,
            String lastName,
            String email,
            String matricule
            ) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (firstName != null && !firstName.isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("firstName")),
                                "%" + firstName.toLowerCase() + "%"
                        )
                );
            }
            if (lastName != null && !lastName.isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("lastName")),
                                "%" + lastName.toLowerCase() + "%"
                        )
                );
            }
            if (email != null && !email.isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("email")),
                                "%" + email.toLowerCase() + "%"
                        )
                );
            }
            if (matricule != null && !matricule.isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("matricule")),
                                "%" + matricule.toLowerCase() + "%"
                        )
                );
            }

// Log the values being passed
            System.out.println("Searching for - firstName: " + firstName + ", lastName: " + lastName +
                    ", email: " + email + ", matricule: " + matricule );

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
