package tn.esprit.pfe.approbation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.pfe.approbation.dtos.UserDto;
import tn.esprit.pfe.approbation.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    List<User> findAll();
    @Query(value = "SELECT u.matricule FROM User u WHERE u.matricule REGEXP '^[0-9]{4}EMP[0-9]{3}$' ORDER BY u.matricule DESC LIMIT 1", nativeQuery = true)
    String findLastMatricule();
    User findByMatricule(String matricule);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u " +
            "WHERE (:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) " +
            "OR (:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) " +
            "OR (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "OR (:matricule IS NULL OR LOWER(u.matricule) LIKE LOWER(CONCAT('%', :matricule, '%'))) " )
    List<User> searchUsers(String firstName, String lastName, String email, String matricule);
    void deleteUserById(Integer id);

    User findUserById(Integer id);
}
