package tn.esprit.pfe.approbation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.pfe.approbation.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAll();
    @Query(value = "SELECT u.matricule FROM User u WHERE u.matricule REGEXP '^[0-9]{4}EMP[0-9]{3}$' ORDER BY u.matricule DESC LIMIT 1", nativeQuery = true)
    String findLastMatricule();
    User findByMatricule(String matricule);
    Optional<User> findByEmail(String email);

}
