package tn.esprit.pfe.approbation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.pfe.approbation.entities.PasswordReset;
import tn.esprit.pfe.approbation.entities.User;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {
    Optional<PasswordReset> findByToken(String token);
    Optional<PasswordReset> findByUser(User user);
    void deleteByUser(User user);
    boolean existsByToken(String token);
}
